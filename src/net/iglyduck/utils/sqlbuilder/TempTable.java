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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.Validate;

/**
 * 创建临时表
 * 
 * @author aglyduck
 */
public class TempTable {

    private List<String> columns = null;

    public TempTable column(String item) {
        Validate.notEmpty(item);

        if (columns == null) {
            columns = new ArrayList<String>();
        }

        columns.add(item);
        return this;
    }

    public TempTable columns(String[] items) {
        for (String item : items) {
            column(item);
        }

        return this;
    }

    private List<Object> froms = null;

    protected TempTable from(Object item) {
        Validate.notNull(item);
        Validate.isTrue(item instanceof WrapTable || item instanceof JoinUnit);
        
        if (froms == null) {
            froms = new ArrayList<Object>();
        }

        if (item instanceof WrapTable) {
            Validate.allElementsOfType(froms, WrapTable.class);
        } else if (item instanceof JoinUnit) {
            Validate.isTrue(froms.isEmpty());
        }
        
        froms.add(item);
        return this;
    }

    public TempTable from(WrapTable item) {
        Validate.notNull(item);
        
        return from((Object)item);
    }
    
    public TempTable from(JoinUnit item) {
        return from((Object)item);
    }

    public TempTable froms(WrapTable[] items) {
        for (Object item : items) {
            from(item);
        }

        return this;        
    }
    
    private ExpressUnit where = null;

    protected TempTable where(String join, Object exp) {
        if (where == null) {
            where = new ExpressUnit();
        }
        
        where = where.isEmpty() ? where.init(exp) : where.join(join, exp);
        return this;
    }
    
    protected TempTable where(String join, String column, String opt, Object columnValue) {
        Object exp = ExpressUnit.toExpress(column, opt, columnValue);
        return this.where(join, exp);
    }
    
    public TempTable whereAnd(String exp) {
        return where("and", exp);
    }
    
    public TempTable whereAnd(ExpressUnit exp) {
        return where("and", exp);
    }
    
    public TempTable whereAnd(String column, String opt, Object columnValue) {
        return where("and", column, opt, columnValue);
    }

    public TempTable whereOr(String exp) {
        return where("or", exp);
    }
    
    public TempTable whereOr(ExpressUnit exp) {
        return where("or", exp);
    }
    
    public TempTable whereOr(String column, String opt, Object columnValue) {
        return where("or", column, opt, columnValue);
    }
    
    private List<String> groups = null;

    public TempTable group(String item) {
        Validate.notEmpty(item);

        if (groups == null) {
            groups = new ArrayList<String>();
        }

        groups.add(item);
        return this;
    }

    public TempTable groups(String[] items) {
        for (String item : items) {
            group(item);
        }

        return this;
    }

    private List<String> orders = null;

    public TempTable order(String item) {
        Validate.notEmpty(item);

        if (orders == null) {
            orders = new ArrayList<String>();
        }

        orders.add(item);
        return this;
    }

    public TempTable order(String item, boolean desc) {
        return order(item + (desc ? " desc" : ""));
    }
    
    public TempTable orders(String[] items) {
        for (String item : items) {
            order(item);
        }

        return this;        
    }

    private String limit = null;

    public TempTable limit(int start, int count) {
        limit = start + ", " + count;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder()
            .append("select ")
            .append(columns.toString().replace("[", "").replace("]", ""))
            .append(" from ").append(froms.toString().replace("[", "").replace("]", ""));

        if (where != null && !where.isEmpty()) {
            s.append(" where ").append(where.toString());
        }

        if (groups != null) {
            s.append(" group by ").append(groups.toString().replace("[", "").replace("]", ""));
        }

        if (orders != null) {
            s.append(" order by ").append(orders.toString().replace("[", "").replace("]", ""));
        }

        if (limit != null) {
            s.append(" limit ").append(limit);
        }

        return s.toString();
    }
}
