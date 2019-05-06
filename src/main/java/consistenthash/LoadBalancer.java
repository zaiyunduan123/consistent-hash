package consistenthash;


/**
 * @Auther: jiangyunxiong
 * @Date: 2019/5/6
 */
public interface LoadBalancer {

    /**
     * 根据当前的 key 通过一致性hash算法的规则取出一个节点
     * @param invocation
     * @return
     */
    Server select(Invocation invocation);

    /**
     * 新增节点
     * @param server
     */
    void add(Server server);

}
