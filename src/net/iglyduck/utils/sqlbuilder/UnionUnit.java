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
 * 合并模块 
 * 用于生成 union 子句 或 union all 子句
 * 
 * @author aglyduck
 */
public class UnionUnit {
    private final static class UnionItem {
        private String union;
        private TempTable item;
        private UnionItem next;

        public UnionItem() {
        }

        public UnionItem(String union) {
            this.union = union;
        }

        public UnionItem item(TempTable item) {
            this.item = item;
            return this;
        }

        public UnionItem getNext() {
            return next;
        }

        public void setNext(UnionItem next) {
            this.next = next;
        }

        public boolean hasNext() {
            return this.next != null;
        }

        @Override
        public String toString() {
            StringBuilder s = new StringBuilder();
            if (StringUtils.isNotEmpty(union)) {
                s.append(" ").append(union).append(" ");
            }

            s.append(item.toString());
            return s.toString();
        }
    }

    private UnionItem head;
    private UnionItem tail;

    public boolean isEmpty() {
        return head == null;
    }

    public UnionUnit init(TempTable t) {
        Validate.notNull(t);
        
        UnionItem item = new UnionItem().item(t);

        head = item;
        tail = item;

        return this;
    }

    protected UnionUnit union(String join, TempTable t) {
        Validate.notEmpty(join);
        Validate.notNull(t);
        Validate.notNull(head);
        
        UnionItem item = new UnionItem(join).item(t);

        tail.setNext(item);
        tail = item;

        return this;        
    }
    
    public UnionUnit union(TempTable t) {
        return union("union", t);
    }

    public UnionUnit unionAll(TempTable t) {
        return union("union all", t);
    }

    @Override
    public String toString() {
        Validate.isTrue(head != tail);

        StringBuilder s = new StringBuilder();

        UnionItem item = head;
        s.append(item.toString());
        while (item.hasNext()) {
            item = item.getNext();
            s.append(item.toString());
        }

        return s.toString();
    }
}
