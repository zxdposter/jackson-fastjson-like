package cn.zxdposter.jackson;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;

/**
 * 对 jackson 的封装
 * 此类有两种作用
 * 1、提供了一些基础的方法
 * 2、对公用的 ObjectMapper 做统一的配置，配合 spring 自定义配置 jackson，尽量做到与 spring 框架中的 jackson 表现相同
 *
 * @author zxd
 */
public abstract class Jackson {
    /**
     * 共用对象，与系统统一设置结合，在 spring 项目中，使用 Jackson2ObjectMapperBuilder.build() 生成.
     */
    protected static ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public static void setObjectMapper(ObjectMapper objectMapper) {
        OBJECT_MAPPER = objectMapper;
    }

    /**
     * java 对象转化成封装的 JacksonObject 对象
     *
     * @param value java 对象，不能传递 String 或其它一些基础的变量
     * @return 封装的 JacksonObject 对象
     */
    public static JacksonObject convertObject(Object value) {
        return OBJECT_MAPPER.convertValue(value, JacksonObject.class);
    }

    /**
     * java 对象转化成封装的 JacksonArray 对象
     *
     * @param value java 对象，不能传递 String 或其它一些基础的变量
     * @return 封装的 JacksonArray 对象
     */
    public static JacksonArray convertArray(Object value) {
        return OBJECT_MAPPER.convertValue(value, JacksonArray.class);
    }

    /**
     * java 对象转化.
     *
     * @param value java 对象
     * @param type  转化类型
     * @param <T>   模版
     * @return 转化后对象
     */
    public static <T> T convert(Object value, Class<T> type) {
        return OBJECT_MAPPER.convertValue(value, type);
    }

    /**
     * java 对象转化
     *
     * @param value         java 对象
     * @param typeReference 能够嵌套模版转化，比如 new TypeReference< Map< String,String>>(){}
     * @param <T>           模版
     * @return 转化后对象
     */
    public static <T> T convert(Object value, TypeReference<T> typeReference) {
        return OBJECT_MAPPER.convertValue(value, typeReference);
    }

    /**
     * java 对象转化成 json string
     *
     * @param object java 对象
     * @return json string
     */
    public static String objectToString(Object object) throws IOException {
        return OBJECT_MAPPER.writeValueAsString(object);
    }

    /**
     * json string 转化成封装 JacksonObject 对象
     *
     * @param text json string
     * @return 封装的 JacksonObject 对象
     */
    public static JacksonObject parseObject(String text) throws IOException {
        if (text == null) {
            return new JacksonObject();
        }
        return new JacksonObject((ObjectNode) OBJECT_MAPPER.readTree(text));
    }

    /**
     * json string 转化成 java 对象
     *
     * @param text json string
     * @return java 对象
     */
    public static <T> T parseJavaObject(String text, TypeReference<T> typeReference) throws IOException {
        return OBJECT_MAPPER.readValue(text, typeReference);
    }


    /**
     * json string 转化成 java 对象
     *
     * @param text json string
     * @return java 对象
     */
    public static <T> T parseJavaObject(String text, Class<T> type) throws IOException {
        return OBJECT_MAPPER.readValue(text, type);
    }

    /**
     * json string 转化成封装 parseArray 对象
     *
     * @param text json string
     * @return 封装的 parseArray 对象
     */
    public static JacksonArray parseArray(String text) throws IOException {
        return new JacksonArray((ArrayNode) OBJECT_MAPPER.readTree(text));
    }

    /**
     * json string 转化成 byte 数组
     *
     * @param object java 对象
     * @return byte 数组
     */
    public static byte[] objectToBytes(Object object) throws IOException {
        return OBJECT_MAPPER.writeValueAsBytes(object);
    }

    /**
     * 转化成 json string
     *
     * @return json string
     */
    public String toJsonString() throws IOException {
        return OBJECT_MAPPER.writeValueAsString(this);
    }

    /**
     * 转化成 json 对象
     *
     * @param type 对象类型
     * @return json 对象
     */
    public <T> T toJava(Class<T> type) {
        return OBJECT_MAPPER.convertValue(this, type);
    }

    /**
     * 转化成 json 对象
     *
     * @param typeReference 对象类型
     * @return json 对象
     */
    public <T> T toJava(TypeReference<T> typeReference) {
        return OBJECT_MAPPER.convertValue(this, typeReference);
    }

    /**
     * 复写 toString，默认输出 json string
     *
     * @return json string
     */
    @Override
    public String toString() {
        try {
            return this.toJsonString();
        } catch (IOException e) {
            return null;
        }
    }
}
