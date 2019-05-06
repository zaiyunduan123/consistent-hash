package consistenthash;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


/**
 * @Auther: jiangyunxiong
 * @Date: 2019/5/6
 */
public class LoadBalanceTest {

    /**
     * 测试新增或删除服务节点后的影响程度
     */
    @Test
    public void testNodeAddAndRemove() {

        int serverNum = 50;//构造50个服务节点
        int invocationNum = 100;//构造100 随机请求
        String[] ips = new String[serverNum];

        for (int i = 0; i < serverNum; i++) {
            ips[i] = IpUtil.getRandomIp();
        }
        LoadBalancer loadBalancer = new ConsistentHashLoadBalancer();
        for (int i = 0; i < serverNum; i++) {
            loadBalancer.add(new Server(ips[i] + ":8080"));
        }

        List<Invocation> invocations = new ArrayList<Invocation>();
        for (int i = 0; i < invocationNum; i++) {
            invocations.add(new Invocation(UUID.randomUUID().toString()));
        }
        int count = 0;
        for (Invocation invocation : invocations) {
            Server origin = loadBalancer.select(invocation);
            loadBalancer.add(new Server(IpUtil.getRandomIp() + ":8080"));
            Server changed = loadBalancer.select(invocation);
            if (origin.getUrl().equals(changed.getUrl())) count++;
        }

        System.out.println(count / Double.valueOf(invocationNum));
    }

}
