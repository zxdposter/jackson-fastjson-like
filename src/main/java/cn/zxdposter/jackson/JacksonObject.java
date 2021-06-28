package cn.zxdposter.jackson;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * 封装 ObjectNode 的一些操作，贴近 fastjson 的写法
 * <p>
 * 使用了 jackson 的指定序列化反序列化函数，使用起来更贴近一个正常的对象，符合 fastjson 的操作习惯
 *
 * @author zxd
 */
public class JacksonObject extends Jackson {

    /**
     * 被封装的 ObjectNode
     */
    private final ObjectNode objectNode;

    /**
     * 序列化指定函数
     *
     * @return 序列化结果
     */
    @JsonValue
    private ObjectNode serialization() {
        return objectNode;
    }

    /**
     * 反序列化指定函数
     *
     * @param value 反序列化数据来源
     * @return 封装的 JacksonObject 对象
     */
    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    private static JacksonObject deserialization(Map<String, Object> value) {
        ObjectNode objectNode = OBJECT_MAPPER.valueToTree(value);
        return new JacksonObject(objectNode);
    }

    /**
     * 提供 ObjectNode 封装
     *
     * @param objectNode 被封装对象
     */
    public JacksonObject(ObjectNode objectNode) {
        this.objectNode = objectNode;
    }

    /**
     * 使用继承来自于 Jackson 的 OBJECT_MAPPER 创建 ObjectNode
     */
    public JacksonObject() {
        this.objectNode = OBJECT_MAPPER.createObjectNode();
    }

    /**
     * 是否为空
     *
     * @return true or false
     */
    public boolean isEmpty() {
        return objectNode.isEmpty();
    }

    /**
     * 判断是否为空
     *
     * @return true or false
     */
    public int size() {
        return objectNode.size();
    }

    /**
     * 判断 key 是否存在，当使用 {@link JacksonObject#put(String, Object)} value 为 null时，该方法返回 true
     *
     * @param key key
     * @return true or false
     */
    public boolean contains(String key) {
        return objectNode.has(key);
    }

    /**
     * 通过 key 获取封装的 JacksonObject
     * <p>
     * 默认 key 的 value 是 ObjectNode 类型，否则会抛出异常类型转换异常，符合 fastjson 的使用习惯
     *
     * @param key key
     * @return 封装的 JacksonObject
     */
    public JacksonObject getJacksonObject(String key) {
        JsonNode value = objectNode.get(key);

        if (value.isObject()) {
            return new JacksonObject((ObjectNode) value);
        }

        return new JacksonObject(OBJECT_MAPPER.valueToTree(value.asText()));
    }

    /**
     * 通过 key 获取封装的 JacksonArray
     * <p>
     * 默认 key 的 value 是 ArrayNode 类型，否则会抛出异常类型转换异常，符合 fastjson 的使用习惯
     *
     * @param key key
     * @return 封装的 JacksonArray
     */
    public JacksonArray getJacksonArray(String key) {
        JsonNode value = objectNode.get(key);

        if (value.isArray()) {
            return new JacksonArray((ArrayNode) value);
        }

        return new JacksonArray(OBJECT_MAPPER.valueToTree(value.asText()));
    }

    /**
     * 通过 key 获取任意类型的数据
     *
     * @param key key
     * @return value
     */
    public Object getObject(String key) {
        JsonNode jsonNode = objectNode.get(key);

        return OBJECT_MAPPER.convertValue(jsonNode, Object.class);
    }

    /**
     * 通过 key 获取 jackson 原生的 JsonNode
     * <p>
     * JsonNode 的类型不仅囊括了 Object Array，还有一些比如 integer double 等一些基本类型，
     * 甚至包括 NullNode，MissingNode 类型，使用上比较全面，因此暴露出来.
     *
     * @param key key
     * @return JsonNode
     */
    public JsonNode getNode(String key) {
        return objectNode.get(key);
    }

    /**
     * 通过 key 获取 java 对象
     *
     * @param key key
     * @return java 对象
     */
    public <T> T getObject(String key, Class<T> clazz) {
        JsonNode jsonNode = objectNode.get(key);

        return OBJECT_MAPPER.convertValue(jsonNode, clazz);
    }

    public <T> T getJavaObject(String key) {
        JsonNode jsonNode = objectNode.get(key);

        return OBJECT_MAPPER.convertValue(jsonNode, new TypeReference<T>() {
        });
    }

    /**
     * 通过 key 获取 java 对象
     *
     * @param key           key
     * @param typeReference 能够嵌套模版转化，比如 new TypeReference< Map< String, String>>(){}
     * @return java 对象
     */
    public <T> T getObject(String key, TypeReference<T> typeReference) {
        JsonNode jsonNode = objectNode.get(key);

        return OBJECT_MAPPER.convertValue(jsonNode, typeReference);
    }

    /**
     * 通过 key 获取 boolean
     * <p>
     * 如果原生对象不是 boolean 类型，以是否有值提供 true or false
     *
     * @param key key
     * @return boolean
     */
    public boolean getBoolean(String key) {
        JsonNode value = objectNode.get(key);

        if (value.isBoolean()) {
            return value.asBoolean();
        }

        return OBJECT_MAPPER.convertValue(value, boolean.class);
    }

    /**
     * 通过 key 获取 byte 数组
     *
     * @param key key
     * @return byte 数组
     */
    public byte[] getBytes(String key) throws IOException {
        JsonNode value = objectNode.get(key);

        if (value.isBinary()) {
            return value.binaryValue();
        } else {
            return null;
        }
    }

    /**
     * 通过 key 获取 short，会经过类型转换，不存在返回 0
     *
     * @param key key
     * @return short
     */
    public short shortValue(String key) {
        JsonNode value = objectNode.get(key);

        if (value.canConvertToInt()) {
            return value.shortValue();
        }

        return OBJECT_MAPPER.convertValue(value, short.class);
    }

    /**
     * 通过 key 获取 int，会经过类型转换，不存在返回 0
     *
     * @param key key
     * @return int
     */
    public int intValue(String key) {
        JsonNode value = objectNode.get(key);

        if (value.canConvertToInt()) {
            return value.intValue();
        }

        return OBJECT_MAPPER.convertValue(value, int.class);
    }

    /**
     * 通过 key 获取 long，会经过类型转换，不存在返回 0
     *
     * @param key key
     * @return long
     */
    public long longValue(String key) {
        JsonNode value = objectNode.get(key);

        if (value.canConvertToLong()) {
            return value.longValue();
        }

        return OBJECT_MAPPER.convertValue(value, long.class);
    }

    /**
     * 通过 key 获取 float，会经过类型转换，不存在返回 0.0
     *
     * @param key key
     * @return float
     */
    public float floatValue(String key) {
        JsonNode value = objectNode.get(key);

        if (value.isFloatingPointNumber()) {
            return value.floatValue();
        }

        return OBJECT_MAPPER.convertValue(value, float.class);
    }

    /**
     * 通过 key 获取 double，会经过类型转换，不存在返回 0.0
     *
     * @param key key
     * @return double
     */
    public double doubleValue(String key) {
        JsonNode value = objectNode.get(key);

        if (value.canConvertToInt()) {
            return value.doubleValue();
        }

        return OBJECT_MAPPER.convertValue(value, double.class);
    }

    /**
     * 通过 key 获取 BigDecimal，会经过类型转换，不存在返回 null
     *
     * @param key key
     * @return BigDecimal
     */
    public BigDecimal getBigDecimal(String key) {
        JsonNode value = objectNode.get(key);

        if (value.isBigDecimal()) {
            return value.decimalValue();
        }

        return OBJECT_MAPPER.convertValue(value, BigDecimal.class);
    }

    /**
     * 通过 key 获取 BigInteger，会经过类型转换，不存在返回 null
     *
     * @param key key
     * @return BigInteger
     */
    public BigInteger getBigInteger(String key) {
        JsonNode value = objectNode.get(key);

        if (value.canConvertToInt()) {
            return value.bigIntegerValue();
        }

        return OBJECT_MAPPER.convertValue(value, BigInteger.class);
    }

    /**
     * 通过 key 获取 string，会经过类型转换，不存在返回 null
     * <p>
     * 虽然 ObjectNode 获取不存在情况下回返回 NullNode，永远不会返回 null，但是 asText 时会返回字符串 "null"
     * 可能会产生误解，导致问题难以排查
     *
     * @param key key
     * @return string
     */
    public String getString(String key) {
        if (objectNode.has(key)) {
            return objectNode.get(key).asText();
        }
        return null;
    }

    /**
     * 通过 key 获取 LocalDateTime，会经过类型转换，不存在返回 null
     * <p>
     * 使用 LocalDateTime 代替 Date
     *
     * @param key key
     * @return LocalDateTime
     */
    public LocalDateTime getDateTime(String key) {
        JsonNode value = objectNode.get(key);

        return OBJECT_MAPPER.convertValue(value, LocalDateTime.class);
    }

    /**
     * 添加键值对，值可以 null
     *
     * @param key   key
     * @param value 任意值
     * @return 自身 JacksonObject
     */
    public JacksonObject put(String key, Object value) {
        objectNode.replace(key, OBJECT_MAPPER.valueToTree(value));
        return this;
    }

    /**
     * 映射值
     *
     * @param key      key
     * @param function 函数
     * @return 映射值
     */
    public <T> T map(String key, Function<Optional<JsonNode>, T> function) {
        return function.apply(Optional.ofNullable(objectNode.get(key)));
    }

    /**
     * 存在操作
     *
     * @param key      key
     * @param consumer 函数
     */
    public void ifPresent(String key, Consumer<JsonNode> consumer) {
        if (objectNode.has(key)) {
            consumer.accept(objectNode.get(key));
        }
    }

    /**
     * 移除元素
     *
     * @param key key
     * @return 自身 JacksonObject
     */
    public JacksonObject remove(String key) {
        objectNode.remove(key);
        return this;
    }
}
