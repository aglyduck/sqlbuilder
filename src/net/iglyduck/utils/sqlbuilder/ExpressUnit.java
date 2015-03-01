package net.iglyduck.utils.sqlbuilder;

/*
 Copyright (c) 2015 aglyduck
 
 Permission is hereby granted, free of charge, to any person obtaining a copy
 of this software and associated documentation files (the "Software"), to deal
 in the Software without restriction, including without limitation the rights
 to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 copies of the Software, and to permit persons to whom the Software is
 furnished to do so, subject to the following conditions:
 
 The above copyright notice and this permission notice shall be included in all
 copies or substantial portions of the Software.
 
 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 SOFTWARE.
 */

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;

/**
 * 表达式模块
 * 用于生成  where子句  或  on子句
 * 
 * new ExpressUnit().init( ... ).and( ... ).toString();
 * new ExpressUnit().init( ... ).or( ... ).toString();
 * 
 * @author aglyduck
 */
public class ExpressUnit {
    private static class ExpressItem {
        private String join;
        private Object item;
        private ExpressItem next;

        public ExpressItem() {
        }

        public ExpressItem(String join) {
            this.join = join;
        }

        public ExpressItem item(Object item) {
            this.item = item;
            return this;
        }

        public ExpressItem getNext() {
            return next;
        }

        public void setNext(ExpressItem next) {
            this.next = next;
        }

        public boolean hasNext() {
            return this.next != null;
        }

        @Override
        public String toString() {
            StringBuilder s = new StringBuilder();

            if (StringUtils.isNotEmpty(join)) {
                s.append(" ").append(join).append(" ");
            }
            
            if (item instanceof String) {
                // String
                s.append(item);
            } else {
                // ExpressUnit
                s.append("(").append(item.toString()).append(")");
            }
            
            return s.toString();
        }
    }

    private ExpressItem head;
    private ExpressItem tail;

    public boolean isEmpty() {
        return head == null;
    }

    public static Object toExpress(String column, String opt, Object columnValue) {
        Validate.notEmpty(column);
        Validate.notEmpty(opt);
        Validate.notNull(columnValue);
        
        if (columnValue instanceof Object[]) {
            Object[] tmp = (Object[]) columnValue;

            ExpressUnit sub = new ExpressUnit();
            for (Object item : tmp) {
                sub.or(column, opt, item);
            }
            
            return sub; // ExpressUnit
        } 
        
        String value;
        if (columnValue instanceof String) {
            String tmp = ((String) columnValue).trim();
            if (tmp.startsWith("(") && tmp.endsWith(")")) {
                value = tmp;
            } else {
                value = "'" + tmp + "'";
            }
        } else {
            value = columnValue.toString();
        }

        return column + " " + opt + " " + value; // String
    }

    protected ExpressUnit init(Object exp) {
        Validate.notNull(exp);
        
        if (exp instanceof String) {
            Validate.notEmpty((String)exp);
        } else if (exp instanceof ExpressUnit) {
            Validate.isTrue(!((ExpressUnit) exp).isEmpty());
        } else {
            Validate.isTrue(false);
        }
        
        ExpressItem item = new ExpressItem().item(exp);

        head = item;
        tail = item;

        return this;
    }
    
    protected ExpressUnit join(String join, Object exp) {
        Validate.notEmpty(join);
        Validate.notNull(exp);
        Validate.isTrue(!isEmpty());

        if (exp instanceof String) {
            Validate.notEmpty((String)exp);
        } else if (exp instanceof ExpressUnit) {
            Validate.isTrue(!((ExpressUnit) exp).isEmpty());
        } else {
            Validate.isTrue(false);
        }
        
        ExpressItem item = new ExpressItem(join).item(exp);

        tail.setNext(item);
        tail = item;

        return this;        
    }
    
    public ExpressUnit and(String exp) {
        return isEmpty() ? init(exp) : join("and", exp);
    }

    public ExpressUnit and(ExpressUnit exp) {
        return isEmpty() ? init(exp) : join("and", exp);
    }

    public ExpressUnit and(String column, String opt, Object columnValue) {
        Object exp = toExpress(column, opt, columnValue);
        return isEmpty() ? init(exp) : join("and", exp);
    }

    public ExpressUnit or(String exp) {
        return isEmpty() ? init(exp) : join("or", exp);
    }

    public ExpressUnit or(ExpressUnit exp) {
        return isEmpty() ? init(exp) : join("or", exp);
    }

    public ExpressUnit or(String column, String opt, Object columnValue) {        
        Object exp = toExpress(column, opt, columnValue);
        return isEmpty() ? init(exp) : join("or", exp);
    }
    
    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        
        ExpressItem item = head;
        s.append(item.toString());
        while (item.hasNext()) {
            item = item.getNext();
            s.append(item.toString());
        }

        return s.toString();
    }
}
