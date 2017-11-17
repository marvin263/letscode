/**
 * Created by nuc on 2015/12/29.
 */
public class Solution {
    public static final String[][] ARRAY = {{"Zero", "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine"},
            {"Twenty", "Thirty", "Forty", "Fifty", "Sixty", "Seventy", "Eighty", "Ninety"},
            {"Ten", "Eleven", "Twelve", "Thirteen", "Fourteen", "Fifteen", "Sixteen", "Seventeen", "Eighteen", "Nineteen"}
    };

    public static class Total {
        public static class Unit {
            public static class Coordinate {
                int x;
                int y;

                public Coordinate(int x, int y) {
                    this.x = x;
                    this.y = y;
                }
            }

            Coordinate hundred;
            Coordinate ten;
            Coordinate single;
        }

        Unit billi;
        Unit milli;
        Unit thousand;
        Unit none;
    }

    public String numberToWords(int num) {
        if (num == 0) {
            return ARRAY[0][0];
        }
        StringBuilder sb = new StringBuilder(128);
        Total t = handleTotal(num);
        total2String(sb, t);
        return sb.substring(1);
    }


    private void total2String(StringBuilder sb, Total t) {
        if (t == null) {
            return;
        }
        unit2String(sb, t.billi, " Billion");
        unit2String(sb, t.milli, " Million");
        unit2String(sb, t.thousand, " Thousand");
        unit2String(sb, t.none, "");
    }

    private void unit2String(StringBuilder sb, Total.Unit u, String bigUnit) {
        if (u == null) {
            return;
        }
        coordinate2String(sb, u.hundred, " Hundred");
        coordinate2String(sb, u.ten, "");
        coordinate2String(sb, u.single, "");
        sb.append(bigUnit);
    }

    private void coordinate2String(StringBuilder sb, Total.Unit.Coordinate c, String smallUnit) {
        if (c == null) {
            return;
        }
        String[][] ARRAY = Solution.ARRAY;
        sb.append(" ");
        sb.append(ARRAY[c.x][c.y]);
        sb.append(smallUnit);
    }

    private Total handleTotal(int a) {
        int billi = 1000000000;
        int milli = 1000000;
        int thousand = 1000;
        Total t = new Total();
        int get;
        int rmd = a;
        while (true) {
            // >= billi
            if (rmd >= billi) {
                get = rmd / billi;
                t.billi = handleUnit(get);
                rmd = rmd % billi;
            }
            // >= milli
            else if (rmd >= milli) {
                get = rmd / milli;
                t.milli = handleUnit(get);
                rmd = rmd % milli;
            }
            // >= thansand
            else if (rmd >= thousand) {
                get = rmd / thousand;
                t.thousand = handleUnit(get);
                rmd = rmd % thousand;
            }
            // >= 0
            else if (rmd >= 1) {
                t.none = handleUnit(rmd);
                break;
            }
            // 0
            else {
                break;
            }
        }
        return t;
    }

    //<=999
    private Total.Unit handleUnit(int a) {
        Total.Unit u = new Total.Unit();
        int get;
        int rmd = a;
        boolean containSingle = true;
        while (true) {
            //100--999
            if (rmd >= 100) {
                get = rmd / 100;
                u.hundred = new Total.Unit.Coordinate(0, get);
                rmd = rmd % 100;
            }
            // 99--20
            else if (rmd >= 20) {
                get = rmd / 10;
                u.ten = new Total.Unit.Coordinate(1, get - 2);
                rmd = rmd % 10;
            }
            // 10--19
            else if (rmd >= 10) {
                rmd = rmd % 10;
                u.ten = new Total.Unit.Coordinate(2, rmd);
                containSingle = false;
            }
            // 1--9
            else if (rmd >= 1) {
                if (containSingle) {
                    u.single = new Total.Unit.Coordinate(0, rmd);
                }
                break;
            }
            // 0
            else {
                break;
            }
        }
        return u;
    }

    public static void main(String[] args) {
        int num = 2111111111;
        String str = new Solution().numberToWords(num);
        System.out.println(str.length());
    }
}
