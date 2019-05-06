package consistenthash;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.TreeMap;

/**
 * @Auther: jiangyunxiong
 * @Date: 2019/5/6
 */
public class ConsistentHashLoadBalancer implements LoadBalancer {

    private TreeMap<Long, Server> virtualNodeRing = new TreeMap<>();

    /**
     * 虚拟节点数量
     */
    private final static int VIRTUAL_NODE_SIZE = 2;
    private final static String VIRTUAL_NODE_SUFFIX = "#";

    @Override
    public Server select(Invocation invocation) {
        Long invocationHashCode = getHashCode(invocation.getHashKey());
        // 沿环顺时针走，第一台遇到的服务器就是其应该定位到的服务器 (返回hashcode大于或等于invocationHashCode的hashcode-server映射)
        Map.Entry<Long, Server> locateEntry = virtualNodeRing.ceilingEntry(invocationHashCode);
        if (locateEntry == null) {
            locateEntry = virtualNodeRing.firstEntry();
        }
        return locateEntry.getValue();
    }


    @Override
    public void add(Server server) {
        // 为了解决Hash环的数据倾斜问题，加入虚拟节点，通过服务器IP或主机名的后面增加编号来实现
        for (int i = 0; i < VIRTUAL_NODE_SIZE; i++) {
            virtualNodeRing.put(getHashCode(server.getUrl() + VIRTUAL_NODE_SUFFIX + i), server);
        }
    }


    private Long getHashCode(String origin) {
        MessageDigest md5;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5 not supported", e);
        }
        md5.reset();
        byte[] keyBytes = null;
        try {
            keyBytes = origin.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Unknown string :" + origin, e);
        }

        md5.update(keyBytes);
        byte[] digest = md5.digest();

        long hashCode = ((long) (digest[3] & 0xFF) << 24)
                | ((long) (digest[2] & 0xFF) << 16)
                | ((long) (digest[1] & 0xFF) << 8)
                | (digest[0] & 0xFF);

        long truncateHashCode = hashCode & 0xffffffffL;
        return truncateHashCode;
    }
}
