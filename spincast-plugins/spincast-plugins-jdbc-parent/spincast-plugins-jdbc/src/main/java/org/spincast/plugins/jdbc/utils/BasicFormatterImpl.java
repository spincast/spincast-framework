/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
//package org.hibernate.engine.jdbc.internal;
package org.spincast.plugins.jdbc.utils;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Set;
import java.util.StringTokenizer;


/**
 * Performs formatting of basic SQL statements (DML + query).
 *
 * @author Gavin King
 * @author Steve Ebersole
 */
public class BasicFormatterImpl {

    public static final String WHITESPACE = " \n\r\f\t";

    private static final Set<String> BEGIN_CLAUSES = new HashSet<String>();
    private static final Set<String> END_CLAUSES = new HashSet<String>();
    private static final Set<String> LOGICAL = new HashSet<String>();
    private static final Set<String> QUANTIFIERS = new HashSet<String>();
    private static final Set<String> DML = new HashSet<String>();
    private static final Set<String> MISC = new HashSet<String>();

    static {
        BEGIN_CLAUSES.add("left");
        BEGIN_CLAUSES.add("right");
        BEGIN_CLAUSES.add("inner");
        BEGIN_CLAUSES.add("outer");
        BEGIN_CLAUSES.add("group");
        BEGIN_CLAUSES.add("order");

        END_CLAUSES.add("where");
        END_CLAUSES.add("set");
        END_CLAUSES.add("having");
        END_CLAUSES.add("join");
        END_CLAUSES.add("from");
        END_CLAUSES.add("by");
        END_CLAUSES.add("join");
        END_CLAUSES.add("into");
        END_CLAUSES.add("union");

        LOGICAL.add("and");
        LOGICAL.add("or");
        LOGICAL.add("when");
        LOGICAL.add("else");
        LOGICAL.add("end");

        QUANTIFIERS.add("in");
        QUANTIFIERS.add("all");
        QUANTIFIERS.add("exists");
        QUANTIFIERS.add("some");
        QUANTIFIERS.add("any");

        DML.add("insert");
        DML.add("update");
        DML.add("delete");

        MISC.add("select");
        MISC.add("on");
    }

    private static final String INDENT_STRING = "    ";
    private static final String INITIAL = System.lineSeparator();

    public String format(String source) {
        return new FormatProcess(source).perform() + System.lineSeparator();
    }

    private static class FormatProcess {

        boolean beginLine = true;
        boolean afterBeginBeforeEnd;
        boolean afterByOrSetOrFromOrSelect;


        @SuppressWarnings("unused")
        boolean afterValues;
        boolean afterOn;
        boolean afterBetween;
        boolean afterInsert;
        int inFunction;
        int parensSinceSelect;
        private LinkedList<Integer> parenCounts = new LinkedList<Integer>();
        private LinkedList<Boolean> afterByOrFromOrSelects = new LinkedList<Boolean>();

        int indent = 0;

        StringBuilder result = new StringBuilder();
        StringTokenizer tokens;
        String lastToken;
        String token;
        String lcToken;

        public FormatProcess(String sql) {
            this.tokens = new StringTokenizer(
                                              sql,
                                              "()+*/-=<>'`\"[]," + WHITESPACE,
                                              true);
        }

        public String perform() {

            this.result.append(INITIAL);

            while (this.tokens.hasMoreTokens()) {
                this.token = this.tokens.nextToken();
                this.lcToken = this.token.toLowerCase(Locale.ROOT);

                if ("'".equals(this.token)) {
                    String t;
                    do {
                        t = this.tokens.nextToken();
                        this.token += t;
                    }
                    // cannot handle single quotes
                    while (!"'".equals(t) && this.tokens.hasMoreTokens());
                } else if ("\"".equals(this.token)) {
                    String t;
                    do {
                        t = this.tokens.nextToken();
                        this.token += t;
                    } while (!"\"".equals(t));
                }

                if (this.afterByOrSetOrFromOrSelect && ",".equals(this.token)) {
                    commaAfterByOrFromOrSelect();
                } else if (this.afterOn && ",".equals(this.token)) {
                    commaAfterOn();
                }

                else if ("(".equals(this.token)) {
                    openParen();
                } else if (")".equals(this.token)) {
                    closeParen();
                }

                else if (BEGIN_CLAUSES.contains(this.lcToken)) {
                    beginNewClause();
                }

                else if (END_CLAUSES.contains(this.lcToken)) {
                    endNewClause();
                }

                else if ("select".equals(this.lcToken)) {
                    select();
                }

                else if (DML.contains(this.lcToken)) {
                    updateOrInsertOrDelete();
                }

                else if ("values".equals(this.lcToken)) {
                    values();
                }

                else if ("on".equals(this.lcToken)) {
                    on();
                }

                else if (this.afterBetween && this.lcToken.equals("and")) {
                    misc();
                    this.afterBetween = false;
                }

                else if (LOGICAL.contains(this.lcToken)) {
                    logical();
                }

                else if (isWhitespace(this.token)) {
                    white();
                }

                else {
                    misc();
                }

                if (!isWhitespace(this.token)) {
                    this.lastToken = this.lcToken;
                }

            }
            return this.result.toString();
        }

        private void commaAfterOn() {
            out();
            this.indent--;
            newline();
            this.afterOn = false;
            this.afterByOrSetOrFromOrSelect = true;
        }

        private void commaAfterByOrFromOrSelect() {
            out();
            newline();
        }

        private void logical() {
            if ("end".equals(this.lcToken)) {
                this.indent--;
            }
            newline();
            out();
            this.beginLine = false;
        }

        private void on() {
            this.indent++;
            this.afterOn = true;
            newline();
            out();
            this.beginLine = false;
        }

        private void misc() {
            out();
            if ("between".equals(this.lcToken)) {
                this.afterBetween = true;
            }
            if (this.afterInsert) {
                newline();
                this.afterInsert = false;
            } else {
                this.beginLine = false;
                if ("case".equals(this.lcToken)) {
                    this.indent++;
                }
            }
        }

        private void white() {
            if (!this.beginLine) {
                this.result.append(" ");
            }
        }

        private void updateOrInsertOrDelete() {
            out();
            this.indent++;
            this.beginLine = false;
            if ("update".equals(this.lcToken)) {
                newline();
            }
            if ("insert".equals(this.lcToken)) {
                this.afterInsert = true;
            }
        }

        private void select() {
            out();
            this.indent++;
            newline();
            this.parenCounts.addLast(this.parensSinceSelect);
            this.afterByOrFromOrSelects.addLast(this.afterByOrSetOrFromOrSelect);
            this.parensSinceSelect = 0;
            this.afterByOrSetOrFromOrSelect = true;
        }

        private void out() {
            this.result.append(this.token);
        }

        private void endNewClause() {
            if (!this.afterBeginBeforeEnd) {
                this.indent--;
                if (this.afterOn) {
                    this.indent--;
                    this.afterOn = false;
                }
                newline();
            }
            out();
            if (!"union".equals(this.lcToken)) {
                this.indent++;
            }
            newline();
            this.afterBeginBeforeEnd = false;
            this.afterByOrSetOrFromOrSelect =
                    "by".equals(this.lcToken) || "set".equals(this.lcToken) || "from".equals(this.lcToken);
        }

        private void beginNewClause() {
            if (!this.afterBeginBeforeEnd) {
                if (this.afterOn) {
                    this.indent--;
                    this.afterOn = false;
                }
                this.indent--;
                newline();
            }
            out();
            this.beginLine = false;
            this.afterBeginBeforeEnd = true;
        }

        private void values() {
            this.indent--;
            newline();
            out();
            this.indent++;
            newline();
            this.afterValues = true;
        }

        private void closeParen() {
            this.parensSinceSelect--;
            if (this.parensSinceSelect < 0) {
                this.indent--;
                this.parensSinceSelect = this.parenCounts.removeLast();
                this.afterByOrSetOrFromOrSelect = this.afterByOrFromOrSelects.removeLast();
            }
            if (this.inFunction > 0) {
                this.inFunction--;
                out();
            } else {
                if (!this.afterByOrSetOrFromOrSelect) {
                    this.indent--;
                    newline();
                }
                out();
            }
            this.beginLine = false;
        }

        private void openParen() {
            if (isFunctionName(this.lastToken) || this.inFunction > 0) {
                this.inFunction++;
            }
            this.beginLine = false;
            if (this.inFunction > 0) {
                out();
            } else {
                out();
                if (!this.afterByOrSetOrFromOrSelect) {
                    this.indent++;
                    newline();
                    this.beginLine = true;
                }
            }
            this.parensSinceSelect++;
        }

        private static boolean isFunctionName(String tok) {
            final char begin = tok.charAt(0);
            final boolean isIdentifier = Character.isJavaIdentifierStart(begin) || '"' == begin;
            return isIdentifier &&
                   !LOGICAL.contains(tok) &&
                   !END_CLAUSES.contains(tok) &&
                   !QUANTIFIERS.contains(tok) &&
                   !DML.contains(tok) &&
                   !MISC.contains(tok);
        }

        private static boolean isWhitespace(String token) {
            return WHITESPACE.contains(token);
        }

        private void newline() {
            this.result.append(System.lineSeparator());
            for (int i = 0; i < this.indent; i++) {
                this.result.append(INDENT_STRING);
            }
            this.beginLine = true;
        }
    }

}
