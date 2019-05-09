package org.spincast.plugins.jsclosurecompiler.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.List;
import java.util.UUID;

import org.junit.Test;
import org.spincast.plugins.jsclosurecompiler.tests.utils.JsClosureCompileTestBase;
import org.spincast.shaded.org.apache.commons.io.FileUtils;

import com.google.common.collect.Lists;

public class JsClosureCompilerTest extends JsClosureCompileTestBase {

    @Test
    public void asString() throws Exception {

        // @formatter:off
        String jsContent = "function titi(aaa , bbb ,ccc ) {\n" +
                           "    return aaa + bbb ;\n" +
                           "} ";
        // @formatter:on

        String result = getSpincastJsClosureCompilerManager().compile(jsContent);
        assertNotNull(result);
        assertEquals("function titi(a,b,c){return a+b};\n", result);
    }

    @Test
    public void asStringWithArgs() throws Exception {

        // @formatter:off
        String jsContent = "function titi(aaa , bbb ,ccc ) {\n" +
                           "    return aaa + bbb ;\n" +
                           "} ";
        // @formatter:on

        String result = getSpincastJsClosureCompilerManager().compile(jsContent, "-O", "WHITESPACE_ONLY");
        assertNotNull(result);
        assertEquals("function titi(aaa,bbb,ccc){return aaa+bbb};\n", result);
    }

    @Test
    public void asStringWithArgs2() throws Exception {

        // @formatter:off
        String jsContent = "function titi(aaa , bbb ,ccc ) {\n" +
                           "    return aaa + bbb ;\n" +
                           "} ";
        // @formatter:on

        String result =
                getSpincastJsClosureCompilerManager().compile(jsContent, "-O", "WHITESPACE_ONLY", "--formatting=PRETTY_PRINT");
        assertNotNull(result);
        assertEquals("function titi(aaa, bbb, ccc) {\n" +
                     "  return aaa + bbb;\n" +
                     "}\n" +
                     ";\n",
                     result);
    }

    @Test
    public void asStringWithArgs3() throws Exception {

        // @formatter:off
        String jsContent = "function titi(aaa , bbb ,ccc ) {\n" +
                           "    return aaa + bbb ;\n" +
                           "} ";
        // @formatter:on

        String result =
                getSpincastJsClosureCompilerManager().compile(jsContent,
                                                              "--compilation_level=WHITESPACE_ONLY",
                                                              "--formatting=PRETTY_PRINT");
        assertNotNull(result);
        assertEquals("function titi(aaa, bbb, ccc) {\n" +
                     "  return aaa + bbb;\n" +
                     "}\n" +
                     ";\n",
                     result);
    }

    @Test
    public void asStringWithArgsWithEquals() throws Exception {

        // @formatter:off
        String jsContent = "function titi(aaa , bbb ,ccc ) {\n" +
                           "    return aaa + bbb ;\n" +
                           "} ";
        // @formatter:on

        String result =
                getSpincastJsClosureCompilerManager().compile(jsContent,
                                                              "--compilation_level=SIMPLE",
                                                              "--formatting=PRETTY_PRINT");
        assertNotNull(result);
        assertEquals("function titi(a, b, c) {\n" +
                     "  return a + b;\n" +
                     "}\n" +
                     ";\n",
                     result);
    }

    @Test
    public void asStringWithArgsWithEquals2() throws Exception {

        // @formatter:off
        String jsContent = "function titi(aaa , bbb ,ccc ) {\n" +
                           "    return aaa + bbb ;\n" +
                           "} ";
        // @formatter:on

        String result =
                getSpincastJsClosureCompilerManager().compile(jsContent,
                                                              "--compilation_level=WHITESPACE_ONLY",
                                                              "--formatting=PRETTY_PRINT");
        assertNotNull(result);
        assertEquals("function titi(aaa, bbb, ccc) {\n" +
                     "  return aaa + bbb;\n" +
                     "}\n" +
                     ";\n",
                     result);
    }

    @Test
    public void asStringWithArgsSeparated() throws Exception {

        // @formatter:off
        String jsContent = "function titi(aaa , bbb ,ccc ) {\n" +
                           "    return aaa + bbb ;\n" +
                           "} ";
        // @formatter:on

        String result =
                getSpincastJsClosureCompilerManager().compile(jsContent,
                                                              "--compilation_level",
                                                              "WHITESPACE_ONLY",
                                                              "--formatting",
                                                              "PRETTY_PRINT");
        assertNotNull(result);
        assertEquals("function titi(aaa, bbb, ccc) {\n" +
                     "  return aaa + bbb;\n" +
                     "}\n" +
                     ";\n",
                     result);
    }

    @Test
    public void asStringWithArgsSeparated2() throws Exception {

        // @formatter:off
        String jsContent = "function titi(aaa , bbb ,ccc ) {\n" +
                           "    return aaa + bbb ;\n" +
                           "} ";
        // @formatter:on

        String result =
                getSpincastJsClosureCompilerManager().compile(jsContent,
                                                              "--compilation_level",
                                                              "WHITESPACE_ONLY");
        assertNotNull(result);
        assertEquals("function titi(aaa,bbb,ccc){return aaa+bbb};\n",
                     result);
    }

    @Test
    public void outputFileArgIsIgnored() throws Exception {

        // @formatter:off
        String jsContent = "function titi(aaa , bbb ,ccc ) {\n" +
                           "    return aaa + bbb ;\n" +
                           "} ";
        // @formatter:on

        String result =
                getSpincastJsClosureCompilerManager().compile(jsContent, "-O", "WHITESPACE_ONLY", "--js_output_file=nope");
        assertNotNull(result);
        assertEquals("function titi(aaa,bbb,ccc){return aaa+bbb};\n", result);
    }

    @Test
    public void asFile() throws Exception {

        // @formatter:off
        String jsContent = "function titi(aaa , bbb ,ccc ) {\n" +
                           "    return aaa + bbb ;\n" +
                           "} ";
        // @formatter:on

        File file = new File(createTestingFilePath());
        FileUtils.writeStringToFile(file, jsContent, "UTF-8");

        String result = getSpincastJsClosureCompilerManager().compile(file);
        assertNotNull(result);
        assertEquals("function titi(a,b,c){return a+b};\n", result);
    }

    @Test
    public void asFileWithArgs() throws Exception {

        // @formatter:off
        String jsContent = "function titi(aaa , bbb ,ccc ) {\n" +
                           "    return aaa + bbb ;\n" +
                           "} ";
        // @formatter:on

        File file = new File(createTestingFilePath());
        FileUtils.writeStringToFile(file, jsContent, "UTF-8");

        String result = getSpincastJsClosureCompilerManager().compile(file, "-O", "WHITESPACE_ONLY");
        assertNotNull(result);
        assertEquals("function titi(aaa,bbb,ccc){return aaa+bbb};\n", result);
    }

    @Test
    public void multipleFile() throws Exception {

        // @formatter:off
        String jsContent1 = "function titi(aaa , bbb ,ccc ) {\n" +
                           "    return aaa + bbb ;\n" +
                           "} ";
        // @formatter:on

        File file1 = new File(createTestingFilePath());
        FileUtils.writeStringToFile(file1, jsContent1, "UTF-8");

        // @formatter:off
        String jsContent2 = "function toto(ddd , eee ,fff ) {\n" +
                           "    return eee + fff ;\n" +
                           "} ";
        // @formatter:on

        File file2 = new File(createTestingFilePath());
        FileUtils.writeStringToFile(file2, jsContent2, "UTF-8");

        String result = getSpincastJsClosureCompilerManager().compile(Lists.newArrayList(file1, file2));
        assertNotNull(result);
        assertEquals("function titi(b,a,c){return b+a};function toto(b,a,c){return a+c};\n", result);
    }

    @Test
    public void multipleFileWithArgs() throws Exception {

        // @formatter:off
        String jsContent1 = "function titi(aaa , bbb ,ccc ) {\n" +
                           "    return aaa + bbb ;\n" +
                           "} ";
        // @formatter:on

        File file1 = new File(createTestingFilePath());
        FileUtils.writeStringToFile(file1, jsContent1, "UTF-8");

        // @formatter:off
        String jsContent2 = "function toto(ddd , eee ,fff ) {\n" +
                           "    return eee + fff ;\n" +
                           "} ";
        // @formatter:on

        File file2 = new File(createTestingFilePath());
        FileUtils.writeStringToFile(file2, jsContent2, "UTF-8");

        String result = getSpincastJsClosureCompilerManager().compile(Lists.newArrayList(file1, file2), "-O", "WHITESPACE_ONLY");
        assertNotNull(result);
        assertEquals("function titi(aaa,bbb,ccc){return aaa+bbb};function toto(ddd,eee,fff){return eee+fff};\n", result);
    }

    @Test
    public void nullArg() throws Exception {

        // @formatter:off
        String jsContent = "function titi(aaa , bbb ,ccc ) {\n" +
                           "    return aaa + bbb ;\n" +
                           "} ";
        // @formatter:on

        String result = getSpincastJsClosureCompilerManager().compile(jsContent, (List<String>)null);
        assertNotNull(result);
        assertEquals("function titi(a,b,c){return a+b};\n", result);
    }

    @Test
    public void nullArgs() throws Exception {

        // @formatter:off
        String jsContent = "function titi(aaa , bbb ,ccc ) {\n" +
                           "    return aaa + bbb ;\n" +
                           "} ";
        // @formatter:on

        try {
            getSpincastJsClosureCompilerManager().compile(jsContent, null, null);
            fail();
        } catch (Exception ex) {
        }
    }

    @Test
    public void nullArgs2() throws Exception {

        // @formatter:off
        String jsContent = "function titi(aaa , bbb ,ccc ) {\n" +
                           "    return aaa + bbb ;\n" +
                           "} ";
        // @formatter:on

        try {
            getSpincastJsClosureCompilerManager().compile(jsContent, Lists.newArrayList((String)null));
            fail();
        } catch (Exception ex) {
        }
    }

    @Test
    public void custom() throws Exception {

        // @formatter:off
        String jsContent1 = "function titi(aaa , bbb ,ccc ) {\n" +
                           "    return aaa + bbb ;\n" +
                           "} ";
        // @formatter:on

        File parentDir = createTestingDir();

        File file1 = new File(parentDir, UUID.randomUUID().toString() + ".js");
        FileUtils.writeStringToFile(file1, jsContent1, "UTF-8");

        // @formatter:off
        String jsContent2 = "function toto(ddd , eee ,fff ) {\n" +
                           "    return eee + fff ;\n" +
                           "} ";
        // @formatter:on

        File file2 = new File(parentDir, UUID.randomUUID().toString() + ".js");
        FileUtils.writeStringToFile(file2, jsContent2, "UTF-8");

        String result = getSpincastJsClosureCompilerManager().compileCustom(parentDir.getAbsolutePath() + "/*.js");
        assertNotNull(result);

        //==========================================
        // Order not guaranteed
        //==========================================
        assertTrue(result.contains("function titi(b,a,c){return b+a};"));
        assertTrue(result.contains("function toto(b,a,c){return a+c};"));
    }

    @Test
    public void custom2() throws Exception {

        // @formatter:off
        String jsContent1 = "function titi(aaa , bbb ,ccc ) {\n" +
                           "    return aaa + bbb ;\n" +
                           "} ";
        // @formatter:on

        File parentDir = createTestingDir();

        File file1 = new File(parentDir, UUID.randomUUID().toString() + ".jsx"); // not.js !!!
        FileUtils.writeStringToFile(file1, jsContent1, "UTF-8");

        // @formatter:off
        String jsContent2 = "function toto(ddd , eee ,fff ) {\n" +
                           "    return eee + fff ;\n" +
                           "} ";
        // @formatter:on

        File file2 = new File(parentDir, UUID.randomUUID().toString() + ".js");
        FileUtils.writeStringToFile(file2, jsContent2, "UTF-8");

        String result = getSpincastJsClosureCompilerManager().compileCustom(parentDir.getAbsolutePath() + "/*.js");
        assertNotNull(result);
        assertEquals("function toto(c,a,b){return a+b};\n", result);
    }

    @Test
    public void customWithArgs() throws Exception {

        // @formatter:off
        String jsContent1 = "function titi(aaa , bbb ,ccc ) {\n" +
                           "    return aaa + bbb ;\n" +
                           "} ";
        // @formatter:on

        File parentDir = createTestingDir();

        File file1 = new File(parentDir, UUID.randomUUID().toString() + ".js");
        FileUtils.writeStringToFile(file1, jsContent1, "UTF-8");

        // @formatter:off
        String jsContent2 = "function toto(ddd , eee ,fff ) {\n" +
                           "    return eee + fff ;\n" +
                           "} ";
        // @formatter:on

        File file2 = new File(parentDir, UUID.randomUUID().toString() + ".js");
        FileUtils.writeStringToFile(file2, jsContent2, "UTF-8");

        String result = getSpincastJsClosureCompilerManager().compileCustom(parentDir.getAbsolutePath() + "/*.js",
                                                                            "-O",
                                                                            "WHITESPACE_ONLY");
        assertNotNull(result);

        //==========================================
        // Order not guaranteed
        //==========================================
        assertTrue(result.contains("function titi(aaa,bbb,ccc){return aaa+bbb};"));
        assertTrue(result.contains("function toto(ddd,eee,fff){return eee+fff};"));


    }

}


