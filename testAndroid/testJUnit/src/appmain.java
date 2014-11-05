/**
 * Created by wangjia-s on 14-11-5.
 */
public class appmain {
    public static void main(String[] args) {
        DataBaseOp dbop = new DataBaseOp();
        try {
            dbop.genLocalData();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
