package org.spincast.plugins.hotswap.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.Set;
import java.util.UUID;

import org.junit.Ignore;
import org.junit.Test;
import org.spincast.plugins.hotswap.fileswatcher.FileToWatch;
import org.spincast.plugins.hotswap.fileswatcher.HotSwapFilesModificationsListener;
import org.spincast.shaded.org.apache.commons.io.FileUtils;
import org.spincast.testing.core.utils.SpincastTestingUtils;
import org.spincast.testing.junitrunner.RepeatUntilSuccess;

import com.google.common.collect.Sets;

// TODO For some reasons some of those tests fails during
// the release process ("mvn release:perform"), but pass when
// ran manually... Investigate why.
@Ignore
public class HotSwapFilesModificationsListenersTest extends HotSwapTestBase {

    @Test
    public void oneFileOnFileSystem() throws Exception {

        File testFile = new File(createTestingFilePath());
        FileUtils.write(testFile, "1", "UTF-8");

        String content = FileUtils.readFileToString(testFile.getAbsoluteFile(), "UTF-8");
        assertEquals("1", content);

        final int[] flag = new int[]{0};

        HotSwapFilesModificationsListener listener = new HotSwapFilesModificationsListener() {

            @Override
            public boolean isEnabled() {
                return true;
            }

            @Override
            public void fileModified(File modifiedFile) {
                flag[0]++;

                assertNotNull(modifiedFile);
                assertEquals(testFile.getName(), modifiedFile.getName());
            }

            @Override
            public Set<FileToWatch> getFilesToWatch() {
                return Sets.newHashSet(FileToWatch.ofFileSystem(testFile.getAbsolutePath()));
            }
        };
        getHotSwapFilesModificationsWatcher().registerListener(listener);
        assertEquals(0, flag[0]);

        FileUtils.write(testFile, "2", "UTF-8", false);
        Thread.sleep(100);
        content = FileUtils.readFileToString(testFile.getAbsoluteFile(), "UTF-8");
        assertEquals("2", content);
        assertEquals(1, flag[0]);

        FileUtils.write(testFile, "3", "UTF-8", false);
        Thread.sleep(100);
        content = FileUtils.readFileToString(testFile.getAbsoluteFile(), "UTF-8");
        assertEquals("3", content);
        assertEquals(2, flag[0]);

        FileUtils.write(testFile, "4", "UTF-8", false);
        Thread.sleep(100);
        content = FileUtils.readFileToString(testFile.getAbsoluteFile(), "UTF-8");
        assertEquals("4", content);
        assertEquals(3, flag[0]);

        assertEquals(1, getListenersByWatchKey().size());

        //==========================================
        // Removes the listener
        //==========================================
        getHotSwapFilesModificationsWatcher().removeListener(listener);

        FileUtils.write(testFile, "5", "UTF-8", false);
        Thread.sleep(100);
        content = FileUtils.readFileToString(testFile.getAbsoluteFile(), "UTF-8");
        assertEquals("5", content);
        assertEquals(3, flag[0]); // still 3

        assertEquals(0, getListenersByWatchKey().size());
    }

    @Test
    @RepeatUntilSuccess(value = 5, sleep = 100)
    public void twoFilesOneListenerNotOnClasspath() throws Exception {

        File testFile1 = new File(createTestingFilePath());
        FileUtils.write(testFile1, "1", "UTF-8");

        File testFile2 = new File(createTestingFilePath());
        FileUtils.write(testFile2, "a", "UTF-8");

        final int[] flag1 = new int[]{0};
        final int[] flag2 = new int[]{0};

        HotSwapFilesModificationsListener listener = new HotSwapFilesModificationsListener() {

            @Override
            public boolean isEnabled() {
                return true;
            }

            @Override
            public void fileModified(File modifiedFile) {
                if (modifiedFile.getName().equals(testFile1.getName())) {
                    flag1[0]++;
                } else if (modifiedFile.getName().equals(testFile2.getName())) {
                    flag2[0]++;
                }
            }

            @Override
            public Set<FileToWatch> getFilesToWatch() {
                return Sets.newHashSet(FileToWatch.ofFileSystem(testFile1.getAbsolutePath()),
                                       FileToWatch.ofFileSystem(testFile2.getAbsolutePath()));
            }

        };
        getHotSwapFilesModificationsWatcher().registerListener(listener);
        assertEquals(0, flag1[0]);
        assertEquals(0, flag2[0]);

        FileUtils.write(testFile1, "2", "UTF-8", false);
        Thread.sleep(100);
        assertEquals(1, flag1[0]);
        assertEquals(0, flag2[0]);

        FileUtils.write(testFile1, "3", "UTF-8", false);
        Thread.sleep(100);
        assertEquals(2, flag1[0]);
        assertEquals(0, flag2[0]);

        FileUtils.write(testFile2, "b", "UTF-8", false);
        Thread.sleep(100);
        assertEquals(2, flag1[0]);
        assertEquals(1, flag2[0]);

        FileUtils.write(testFile2, "d", "UTF-8", false);
        Thread.sleep(100);
        assertEquals(2, flag1[0]);
        assertEquals(2, flag2[0]);

        //==========================================
        // Removes the listener
        //==========================================
        getHotSwapFilesModificationsWatcher().removeListener(listener);

        FileUtils.write(testFile1, "b", "UTF-8", false);
        Thread.sleep(100);
        assertEquals(2, flag1[0]);
        assertEquals(2, flag2[0]);

        FileUtils.write(testFile2, "d", "UTF-8", false);
        Thread.sleep(100);
        assertEquals(2, flag1[0]);
        assertEquals(2, flag2[0]);
    }

    @Test
    public void twoListenersOnSameFile() throws Exception {

        File testFile1 = new File(createTestingFilePath());
        FileUtils.write(testFile1, "1", "UTF-8");

        File testFile2 = new File(createTestingFilePath());
        FileUtils.write(testFile2, "a", "UTF-8");

        final int[] flag1 = new int[]{0};
        final int[] flag2 = new int[]{0};
        final int[] flag3 = new int[]{0};

        HotSwapFilesModificationsListener listener1 = new HotSwapFilesModificationsListener() {

            @Override
            public boolean isEnabled() {
                return true;
            }

            @Override
            public void fileModified(File modifiedFile) {
                if (modifiedFile.getName().equals(testFile1.getName())) {
                    flag1[0]++;
                } else if (modifiedFile.getName().equals(testFile2.getName())) {
                    flag2[0]++;
                }
            }

            @Override
            public Set<FileToWatch> getFilesToWatch() {
                return Sets.newHashSet(FileToWatch.ofFileSystem(testFile1.getAbsolutePath()),
                                       FileToWatch.ofFileSystem(testFile2.getAbsolutePath()));
            }
        };

        HotSwapFilesModificationsListener listener2 = new HotSwapFilesModificationsListener() {

            @Override
            public boolean isEnabled() {
                return true;
            }

            @Override
            public void fileModified(File modifiedFile) {
                if (modifiedFile.getName().equals(testFile1.getName())) {
                    flag3[0]++;
                } else if (modifiedFile.getName().equals(testFile2.getName())) {
                    flag2[0]++;
                }
            }

            @Override
            public Set<FileToWatch> getFilesToWatch() {
                //==========================================
                // The same "file1", but not "file2"
                //==========================================
                return Sets.newHashSet(FileToWatch.ofFileSystem(testFile1.getAbsolutePath()));
            }
        };

        getHotSwapFilesModificationsWatcher().registerListener(listener1);
        getHotSwapFilesModificationsWatcher().registerListener(listener2);

        //==========================================
        // Also register the listener1 again to make sure it doesn't
        // change anything.
        //==========================================
        getHotSwapFilesModificationsWatcher().registerListener(listener1);

        assertEquals(0, flag1[0]);
        assertEquals(0, flag2[0]);

        FileUtils.write(testFile1, "2", "UTF-8", false);
        Thread.sleep(300);
        assertEquals(1, flag1[0]);
        assertEquals(0, flag2[0]);
        assertEquals(1, flag3[0]);

        FileUtils.write(testFile1, "3", "UTF-8", false);
        Thread.sleep(300);
        assertEquals(2, flag1[0]);
        assertEquals(0, flag2[0]);
        assertEquals(2, flag3[0]);

        FileUtils.write(testFile2, "b", "UTF-8", false);
        Thread.sleep(300);
        assertEquals(2, flag1[0]);
        assertEquals(1, flag2[0]);
        assertEquals(2, flag3[0]);

        //==========================================
        // Removes the first listener
        //==========================================
        getHotSwapFilesModificationsWatcher().removeListener(listener1);

        FileUtils.write(testFile1, "4", "UTF-8", false);
        Thread.sleep(300);
        assertEquals(2, flag1[0]);
        assertEquals(1, flag2[0]);
        assertEquals(3, flag3[0]);

        // No listener on the file anymore
        FileUtils.write(testFile2, "b", "UTF-8", false);
        Thread.sleep(300);
        assertEquals(2, flag1[0]);
        assertEquals(1, flag2[0]);
        assertEquals(3, flag3[0]);

        //==========================================
        // Removes the second listener
        //==========================================
        getHotSwapFilesModificationsWatcher().removeListener(listener2);

        FileUtils.write(testFile1, "4", "UTF-8", false);
        Thread.sleep(300);
        assertEquals(2, flag1[0]);
        assertEquals(1, flag2[0]);
        assertEquals(3, flag3[0]);

        FileUtils.write(testFile2, "b", "UTF-8", false);
        Thread.sleep(300);
        assertEquals(2, flag1[0]);
        assertEquals(1, flag2[0]);
        assertEquals(3, flag3[0]);
    }

    @Test
    public void twoListenersSameDir() throws Exception {

        File testFile1 = new File(createTestingFilePath());
        FileUtils.write(testFile1, "x", "UTF-8");

        File testFile2 = new File(createTestingFilePath());
        FileUtils.write(testFile2, "x", "UTF-8");

        final int[] flag1 = new int[]{0};
        final int[] flag2 = new int[]{0};

        HotSwapFilesModificationsListener listener1 = new HotSwapFilesModificationsListener() {

            @Override
            public boolean isEnabled() {
                return true;
            }

            @Override
            public void fileModified(File modifiedFile) {
                if (modifiedFile.getName().equals(testFile1.getName())) {
                    flag1[0]++;
                } else if (modifiedFile.getName().equals(testFile2.getName())) {
                    flag2[0]++;
                }
            }

            @Override
            public Set<FileToWatch> getFilesToWatch() {
                //==========================================
                // testFile1
                //==========================================
                return Sets.newHashSet(FileToWatch.ofFileSystem(testFile1.getAbsolutePath()));
            }
        };

        HotSwapFilesModificationsListener listener2 = new HotSwapFilesModificationsListener() {

            @Override
            public boolean isEnabled() {
                return true;
            }

            @Override
            public void fileModified(File modifiedFile) {
                if (modifiedFile.getName().equals(testFile1.getName())) {
                    flag1[0]++;
                } else if (modifiedFile.getName().equals(testFile2.getName())) {
                    flag2[0]++;
                }
            }

            @Override
            public Set<FileToWatch> getFilesToWatch() {
                //==========================================
                // testFile2
                //==========================================
                return Sets.newHashSet(FileToWatch.ofFileSystem(testFile2.getAbsolutePath()));
            }
        };

        getHotSwapFilesModificationsWatcher().registerListener(listener1);
        getHotSwapFilesModificationsWatcher().registerListener(listener2);

        assertEquals(0, flag1[0]);
        assertEquals(0, flag2[0]);

        FileUtils.write(testFile1, "x", "UTF-8", true);
        Thread.sleep(300);
        assertEquals(1, flag1[0]);
        assertEquals(0, flag2[0]);

        FileUtils.write(testFile1, "x", "UTF-8", true);
        Thread.sleep(300);
        assertEquals(2, flag1[0]);
        assertEquals(0, flag2[0]);

        FileUtils.write(testFile2, "x", "UTF-8", true);
        Thread.sleep(300);
        assertEquals(2, flag1[0]);
        assertEquals(1, flag2[0]);

        FileUtils.write(testFile2, "x", "UTF-8", true);
        Thread.sleep(300);
        assertEquals(2, flag1[0]);
        assertEquals(2, flag2[0]);

        getHotSwapFilesModificationsWatcher().removeListener(listener1);

        FileUtils.write(testFile1, "x", "UTF-8", true);
        Thread.sleep(300);
        assertEquals(2, flag1[0]);
        assertEquals(2, flag2[0]);

        FileUtils.write(testFile2, "x", "UTF-8", true);
        Thread.sleep(300);
        assertEquals(2, flag1[0]);
        assertEquals(3, flag2[0]);

        getHotSwapFilesModificationsWatcher().removeListener(listener2);

        FileUtils.write(testFile2, "x", "UTF-8", true);
        Thread.sleep(300);
        assertEquals(2, flag1[0]);
        assertEquals(3, flag2[0]);
    }

    @Test
    @RepeatUntilSuccess(value = 5, sleep = 100)
    public void addListenerAfterAWhile() throws Exception {

        File testFile1 = new File(createTestingFilePath());
        FileUtils.write(testFile1, "x", "UTF-8");

        final int[] flag1 = new int[]{0};

        HotSwapFilesModificationsListener listener1 = new HotSwapFilesModificationsListener() {

            @Override
            public boolean isEnabled() {
                return true;
            }

            @Override
            public void fileModified(File modifiedFile) {
                if (modifiedFile.getName().equals(testFile1.getName())) {
                    flag1[0]++;
                }
            }

            @Override
            public Set<FileToWatch> getFilesToWatch() {
                //==========================================
                // testFile1
                //==========================================
                return Sets.newHashSet(FileToWatch.ofFileSystem(testFile1.getAbsolutePath()));
            }
        };

        HotSwapFilesModificationsListener listener2 = new HotSwapFilesModificationsListener() {

            @Override
            public boolean isEnabled() {
                return true;
            }

            @Override
            public void fileModified(File modifiedFile) {
                if (modifiedFile.getName().equals(testFile1.getName())) {
                    flag1[0]++;
                }
            }

            @Override
            public Set<FileToWatch> getFilesToWatch() {
                //==========================================
                // testFile2
                //==========================================
                return Sets.newHashSet(FileToWatch.ofFileSystem(testFile1.getAbsolutePath()));
            }
        };

        getHotSwapFilesModificationsWatcher().registerListener(listener1);

        assertEquals(0, flag1[0]);

        FileUtils.write(testFile1, "x", "UTF-8", true);
        Thread.sleep(300);
        assertEquals(1, flag1[0]);

        FileUtils.write(testFile1, "x", "UTF-8", true);
        Thread.sleep(300);
        assertEquals(2, flag1[0]);

        getHotSwapFilesModificationsWatcher().registerListener(listener2);

        FileUtils.write(testFile1, "x", "UTF-8", true);
        Thread.sleep(300);
        assertEquals(4, flag1[0]);

        FileUtils.write(testFile1, "x", "UTF-8", true);
        Thread.sleep(300);
        assertEquals(6, flag1[0]);

        getHotSwapFilesModificationsWatcher().removeListener(listener1);

        FileUtils.write(testFile1, "x", "UTF-8", true);
        Thread.sleep(300);
        assertEquals(7, flag1[0]);
    }

    @Test
    public void fileDoesntExist() throws Exception {

        final int[] flag = new int[]{0};

        HotSwapFilesModificationsListener listener = new HotSwapFilesModificationsListener() {

            @Override
            public boolean isEnabled() {
                return true;
            }

            @Override
            public void fileModified(File modifiedFile) {
                flag[0]++;
            }

            @Override
            public Set<FileToWatch> getFilesToWatch() {
                return Sets.newHashSet(FileToWatch.ofFileSystem(createTestingFilePath() + UUID.randomUUID().toString()));
            }
        };

        try {
            getHotSwapFilesModificationsWatcher().registerListener(listener);
            fail();
        } catch (Exception ex) {
        }
    }

    @Test
    public void classpathFile() throws Exception {

        final int[] flag1 = new int[]{0};

        String classpathFilePath = "spincast-plugins-hotswap-tests/test.txt";
        File file = SpincastTestingUtils.getClasspathFileNotInJar(classpathFilePath);
        assertNotNull(file);

        HotSwapFilesModificationsListener listener = new HotSwapFilesModificationsListener() {

            @Override
            public boolean isEnabled() {
                return true;
            }

            @Override
            public void fileModified(File modifiedFile) {
                flag1[0]++;

                assertNotNull(modifiedFile);
                assertEquals("test.txt", modifiedFile.getName());
            }

            @Override
            public Set<FileToWatch> getFilesToWatch() {
                return Sets.newHashSet(FileToWatch.ofClasspath(classpathFilePath));
            }
        };
        assertEquals(0, flag1[0]);

        getHotSwapFilesModificationsWatcher().registerListener(listener);

        assertEquals(1, getListenersByWatchKey().size());

        FileUtils.write(file, "x", "UTF-8", true);
        Thread.sleep(2000);
        assertEquals(1, flag1[0]); // TODO Error here while releasing, why?

        //==========================================
        // Resets test file
        //==========================================
        FileUtils.write(file, "test", "UTF-8", false);
    }

    @Test
    public void regExFileSystem() throws Exception {

        final int[] flag0 = new int[]{0};
        final int[] flag1 = new int[]{0};
        final int[] flag2 = new int[]{0};
        final int[] flag3 = new int[]{0};
        final int[] flag4 = new int[]{0};

        File testDir = new File(getTestingWritableTempDir(), UUID.randomUUID().toString());
        boolean created = testDir.mkdirs();
        assertTrue(created);

        File file1 = new File(testDir, "test1.txt");
        FileUtils.write(file1, "x", "UTF-8");

        File file2 = new File(testDir, "test2.txt");
        FileUtils.write(file2, "x", "UTF-8");

        File file3 = new File(testDir, "test3.yaml");
        FileUtils.write(file3, "x", "UTF-8");

        File file4 = new File(testDir, "toto.txt");
        FileUtils.write(file4, "x", "UTF-8");

        HotSwapFilesModificationsListener listener = new HotSwapFilesModificationsListener() {

            @Override
            public boolean isEnabled() {
                return true;
            }

            @Override
            public void fileModified(File modifiedFile) {
                if (modifiedFile.getName().equals(file1.getName())) {
                    flag1[0]++;
                } else if (modifiedFile.getName().equals(file2.getName())) {
                    flag2[0]++;
                } else if (modifiedFile.getName().equals(file3.getName())) {
                    flag3[0]++;
                } else if (modifiedFile.getName().equals(file4.getName())) {
                    flag4[0]++;
                } else {
                    flag0[0]++;
                }
            }

            @Override
            public Set<FileToWatch> getFilesToWatch() {
                return Sets.newHashSet(FileToWatch.ofRegEx(testDir.getAbsolutePath(), "test[0-9]+\\.txt", false));
            }
        };
        getHotSwapFilesModificationsWatcher().registerListener(listener);

        FileUtils.write(file1, "x", "UTF-8", true);
        Thread.sleep(300);
        assertEquals(0, flag0[0]);
        assertEquals(1, flag1[0]);
        assertEquals(0, flag2[0]);
        assertEquals(0, flag3[0]);
        assertEquals(0, flag4[0]);

        FileUtils.write(file2, "x", "UTF-8", true);
        Thread.sleep(300);
        assertEquals(0, flag0[0]);
        assertEquals(1, flag1[0]);
        assertEquals(1, flag2[0]);
        assertEquals(0, flag3[0]);
        assertEquals(0, flag4[0]);

        FileUtils.write(file3, "x", "UTF-8", true);
        Thread.sleep(300);
        assertEquals(0, flag0[0]);
        assertEquals(1, flag1[0]);
        assertEquals(1, flag2[0]);
        assertEquals(0, flag3[0]);
        assertEquals(0, flag4[0]);

        FileUtils.write(file4, "x", "UTF-8", true);
        Thread.sleep(300);
        assertEquals(0, flag0[0]);
        assertEquals(1, flag1[0]);
        assertEquals(1, flag2[0]);
        assertEquals(0, flag3[0]);
        assertEquals(0, flag4[0]);
    }

    @Test
    public void regExClasspath() throws Exception {

        final int[] flag1 = new int[]{0};

        String classpathFilePath = "spincast-plugins-hotswap-tests/test.txt";
        File file = SpincastTestingUtils.getClasspathFileNotInJar(classpathFilePath);
        assertNotNull(file);

        HotSwapFilesModificationsListener listener = new HotSwapFilesModificationsListener() {

            @Override
            public boolean isEnabled() {
                return true;
            }

            @Override
            public void fileModified(File modifiedFile) {
                flag1[0]++;

                assertNotNull(modifiedFile);
                assertEquals("test.txt", modifiedFile.getName());
            }

            @Override
            public Set<FileToWatch> getFilesToWatch() {
                return Sets.newHashSet(FileToWatch.ofRegEx("/spincast-plugins-hotswap-tests", "tes[tsj]+\\.txt", true));
            }
        };
        assertEquals(0, flag1[0]);

        getHotSwapFilesModificationsWatcher().registerListener(listener);

        assertEquals(1, getListenersByWatchKey().size());

        FileUtils.write(file, "x", "UTF-8", true);
        Thread.sleep(300);
        assertEquals(1, flag1[0]);

        //==========================================
        // Resets test file
        //==========================================
        FileUtils.write(file, "test", "UTF-8", false);
    }

    @Test
    public void listenerNotEnabled() throws Exception {

        File testFile = new File(createTestingFilePath());
        FileUtils.write(testFile, "1", "UTF-8");

        String content = FileUtils.readFileToString(testFile.getAbsoluteFile(), "UTF-8");
        assertEquals("1", content);

        final int[] flag = new int[]{0};

        HotSwapFilesModificationsListener listener = new HotSwapFilesModificationsListener() {

            @Override
            public boolean isEnabled() {
                return false; // FALSE
            }

            @Override
            public void fileModified(File modifiedFile) {
                flag[0]++;
            }

            @Override
            public Set<FileToWatch> getFilesToWatch() {
                return Sets.newHashSet(FileToWatch.ofFileSystem(testFile.getAbsolutePath()));
            }
        };
        getHotSwapFilesModificationsWatcher().registerListener(listener);
        assertEquals(0, flag[0]);
        assertEquals(0, getListenersByWatchKey().size());

        FileUtils.write(testFile, "x", "UTF-8", true);
        Thread.sleep(300);
        assertEquals(0, flag[0]);
        assertEquals(0, getListenersByWatchKey().size());
    }

}
