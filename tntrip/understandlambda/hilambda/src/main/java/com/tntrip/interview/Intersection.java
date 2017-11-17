package com.tntrip.interview;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Created by libing2 on 2016/10/11.
 */
public class Intersection {
    interface A0 {
        void m() throws CloneNotSupportedException, IOException;
    }

    interface A1 {
        void m() throws SQLException, IOException;
    }

    interface A2 extends A0, A1 {
        void m();
    }
}
