package cn.zxdposter.jackson;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;
import lombok.SneakyThrows;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 封装 ArrayNode 的一些操作，贴近 fastjson 的写法
 * <p>
 * 使用了 jackson 的指定序列化反序列化函数，使用起来更贴近一个正常的对象，符合 fastjson 的操作习惯
 *
 * @author zxd
 */
public class JacksonArray extends Jackson implements Iterable<JsonNode> {
    /**
     * 被封装的 ArrayNode
     */
    @Getter
    private final ArrayNode arrayNode;

    /**
     * 序列化指定函数
     *
     * @return 序列化结果
     */
    @JsonValue
    private ArrayNode serialization() {
        return arrayNode;
    }

    /**
     * 反序列化指定函数
     *
     * @param value 反序列化数据来源
     * @return 封装的 JacksonArray 对象
     */
    @SneakyThrows
    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    private static JacksonArray deserialization(List<Object> value) {
        ArrayNode arrayNode = OBJECT_MAPPER.valueToTree(value);
        return new JacksonArray(arrayNode);
    }

    /**
     * 提供 ArrayNode 封装
     *
     * @param arrayNode 被封装对象
     */
    public JacksonArray(ArrayNode arrayNode) {
        this.arrayNode = arrayNode;
    }

    /**
     * 使用继承来自于 Jackson 的 OBJECT_MAPPER 创建 ArrayNode
     */
    public JacksonArray() {
        this.arrayNode = OBJECT_MAPPER.createArrayNode();
    }

    /**
     * 获取元素数量
     *
     * @return 元素数量
     */
    public int size() {
        return arrayNode.size();
    }

    /**
     * 是否为空
     *
     * @return true or false
     */
    public boolean isEmpty() {
        return arrayNode.isEmpty();
    }

    /**
     * 判断下标是否存在
     *
     * @param index 下标
     * @return true or false
     */
    public boolean contains(int index) {
        return arrayNode.has(index);
    }

    /**
     * 获取迭代器，用于 for 循环
     *
     * @return 迭代器
     */
    @Override
    public Iterator<JsonNode> iterator() {
        return arrayNode.iterator();
    }

    /**
     * 添加元素，可以为 null
     *
     * @return 自身 JacksonArray
     */
    public JacksonArray add(Object... e) {
        for (Object o : e) {
            arrayNode.add(OBJECT_MAPPER.valueToTree(o));
        }
        return this;
    }

    /**
     * 移除元素
     *
     * @param index 下标
     * @return 自身 JacksonArray
     */
    public JacksonArray remove(int index) {
        arrayNode.remove(index);
        return this;
    }

    /**
     * 移除所有元素
     *
     * @return 自身 JacksonArray
     */
    public JacksonArray removeAll() {
        arrayNode.removeAll();
        return this;
    }

    /**
     * 添加所有元素
     *
     * @param objects 元素列表
     * @return 自身 JacksonArray
     */
    public JacksonArray addAll(Collection<?> objects) {
        List<JsonNode> collect = objects.stream().map(v -> OBJECT_MAPPER.convertValue(v, JsonNode.class))
                .collect(Collectors.toList());
        arrayNode.addAll(collect);
        return this;
    }

    /**
     * 替换元素，如果下标不合法，直接追加元素
     *
     * @param index   原来的下标
     * @param element 元素
     * @return 自身 JacksonArray
     */
    public JacksonArray set(int index, Object element) {
        JsonNode jsonNode = OBJECT_MAPPER.valueToTree(element);

        if (index < 0 || index >= arrayNode.size()) {
            arrayNode.add(jsonNode);
        } else {
            arrayNode.set(index, jsonNode);
        }

        return this;
    }

    /**
     * 判断元素下标
     * <p>
     * ArrayNode 没有实现 indexOf，需要自己写 for 循环判断
     * <p>
     * 找不到返回 -1
     *
     * @param o 目标元素
     * @return 下标
     */
    public int indexOf(Object o) {
        JsonNode jsonNode = OBJECT_MAPPER.valueToTree(o);

        for (int i = 0; i < arrayNode.size(); i++) {
            if (jsonNode.equals(arrayNode.get(i))) {
                return i;
            }
        }

        return -1;
    }

    /**
     * 截取列表
     *
     * @param fromIndex 开始下标
     * @param toIndex   结束下标
     * @return 新列表
     */
    public List<JsonNode> subList(int fromIndex, int toIndex) {
        int size = arrayNode.size();
        if (fromIndex < 0) {
            throw new IndexOutOfBoundsException("fromIndex = " + fromIndex);
        }
        if (toIndex > size) {
            throw new IndexOutOfBoundsException("toIndex = " + toIndex);
        }
        if (fromIndex > toIndex) {
            throw new IllegalArgumentException("fromIndex(" + fromIndex +
                    ") > toIndex(" + toIndex + ")");
        }

        List<JsonNode> list = new ArrayList<>();

        for (int i = fromIndex; i < toIndex; i++) {
            list.add(arrayNode.get(i));
        }
        return list;
    }

    /**
     * 根据下标获取元素
     *
     * @param index 下标
     * @return 下标元素
     */
    public JsonNode get(int index) {
        return arrayNode.get(index);
    }

    /**
     * 通过下标获取封装的 JacksonObject
     * <p>
     * 默认 key 的 value 是 ObjectNode 类型，否则会抛出异常类型转换异常，符合 fastjson 的使用习惯
     *
     * @param index 下标
     * @return 封装的 JacksonObject
     */
    public JacksonObject getJacksonObject(int index) {
        return new JacksonObject((ObjectNode) arrayNode.get(index));
    }

    /**
     * 通过下标获取封装的 JacksonArray
     * <p>
     * 默认 key 的 value 是 ArrayNode 类型，否则会抛出异常类型转换异常，符合 fastjson 的使用习惯
     *
     * @param index 下标
     * @return 封装的 JacksonArray
     */
    public JacksonArray getJacksonArray(int index) {
        return new JacksonArray((ArrayNode) arrayNode.get(index));

    }

    /**
     * 通过下标获取 java 对象
     *
     * @param index 下标
     * @return java 对象
     */
    public <T> T getObject(int index, Class<T> clazz) {
        JsonNode obj = arrayNode.get(index);

        return OBJECT_MAPPER.convertValue(obj, clazz);
    }

    /**
     * 通过下标获取 java 对象
     *
     * @param index         下标
     * @param typeReference 能够嵌套模版转化，比如 new TypeReference< Map< String, String>>(){}
     * @return java 对象
     */
    public <T> T getObject(int index, TypeReference<T> typeReference) {
        JsonNode obj = arrayNode.get(index);

        return OBJECT_MAPPER.convertValue(obj, typeReference);
    }

    /**
     * 通过下标获取 boolean
     * <p>
     * 如果原生对象不是 boolean 类型，以是否有值提供 true or false
     *
     * @param index 下标
     * @return boolean
     */
    public boolean getBoolean(int index) {
        JsonNode value = arrayNode.get(index);

        if (value.isBoolean()) {
            return value.asBoolean();
        }

        return OBJECT_MAPPER.convertValue(value, boolean.class);
    }

    /**
     * 通过下标获取 byte 数组
     *
     * @param index 下标
     * @return byte 数组
     */
    @SneakyThrows
    public byte[] getBytes(int index) {
        JsonNode value = arrayNode.get(index);

        if (value.isBinary()) {
            return value.binaryValue();
        } else {
            return null;
        }
    }

    /**
     * 通过下标获取 short，会经过类型转换，不存在返回 0
     *
     * @param index 下标
     * @return short
     */
    public short shortValue(int index) {
        JsonNode value = arrayNode.get(index);

        if (value.canConvertToInt()) {
            return value.shortValue();
        }

        return OBJECT_MAPPER.convertValue(value, short.class);
    }

    /**
     * 通过下标获取 int，会经过类型转换，不存在返回 0
     *
     * @param index 下标
     * @return int
     */
    public int intValue(int index) {
        JsonNode value = arrayNode.get(index);

        if (value.canConvertToInt()) {
            return value.intValue();
        }

        return OBJECT_MAPPER.convertValue(value, int.class);
    }

    /**
     * 通过下标获取 long，会经过类型转换，不存在返回 0
     *
     * @param index 下标
     * @return long
     */
    public long longValue(int index) {
        JsonNode value = arrayNode.get(index);

        if (value.canConvertToLong()) {
            return value.longValue();
        }

        return OBJECT_MAPPER.convertValue(value, long.class);
    }

    /**
     * 通过下标获取 float，会经过类型转换，不存在返回 0.0
     *
     * @param index 下标
     * @return float
     */
    public float floatValue(int index) {
        JsonNode value = arrayNode.get(index);

        if (value.isFloatingPointNumber()) {
            return value.floatValue();
        }

        return OBJECT_MAPPER.convertValue(value, float.class);
    }

    /**
     * 通过下标获取 double，会经过类型转换，不存在返回 0.0
     *
     * @param index 下标
     * @return double
     */
    public double doubleValue(int index) {
        JsonNode value = arrayNode.get(index);

        if (value.canConvertToInt()) {
            return value.doubleValue();
        }

        return OBJECT_MAPPER.convertValue(value, double.class);
    }

    /**
     * 通过下标获取 BigDecimal，会经过类型转换，不存在返回 null
     *
     * @param index 下标
     * @return BigDecimal
     */
    public BigDecimal getBigDecimal(int index) {
        JsonNode value = arrayNode.get(index);

        if (value.isBigDecimal()) {
            return value.decimalValue();
        }

        return OBJECT_MAPPER.convertValue(value, BigDecimal.class);
    }

    /**
     * 通过下标获取 BigInteger，会经过类型转换，不存在返回 null
     *
     * @param index 下标
     * @return BigInteger
     */
    public BigInteger getBigInteger(int index) {
        JsonNode value = arrayNode.get(index);

        if (value.canConvertToInt()) {
            return value.bigIntegerValue();
        }

        return OBJECT_MAPPER.convertValue(value, BigInteger.class);
    }

    /**
     * 通过下标获取 string，会经过类型转换，不存在返回 null
     * <p>
     * 虽然 ArrayNode 获取不存在情况下回返回 NullNode，永远不会返回 null，但是 asText 时会返回字符串 "null"
     * 可能会产生误解，导致问题难以排查
     *
     * @param index 下标
     * @return string
     */
    public String getString(int index) {
        JsonNode value = arrayNode.get(index);

        return value.asText();
    }

    /**
     * 通过下标获取 LocalDateTime，会经过类型转换，不存在返回 null
     * <p>
     * 使用 LocalDateTime 代替 Date
     *
     * @param index 下标
     * @return LocalDateTime
     */
    public LocalDateTime getDateTime(int index) {
        JsonNode value = arrayNode.get(index);

        return OBJECT_MAPPER.convertValue(value, LocalDateTime.class);
    }

}
