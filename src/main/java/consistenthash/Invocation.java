package consistenthash;

/**
 * @Auther: jiangyunxiong
 * @Date: 2019/5/6
 */
public class Invocation {

    private String hashKey;

    public Invocation(String hashKey){
        this.hashKey = hashKey;
    }

    public String getHashKey() {
        return hashKey;
    }

    public void setHashKey(String hashKey) {
        this.hashKey = hashKey;
    }
}
