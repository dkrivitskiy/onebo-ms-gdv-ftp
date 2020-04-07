package com.one.gdvftp;

import java.util.Map;
import org.jooq.lambda.Seq;
import org.jooq.lambda.tuple.Tuple2;

public class MapUtil {

  public static <K,V>Map<K,V> mapOf(Seq<K> keys, Seq<V> values) {
    return keys.zip(values).toMap(Tuple2::v1, Tuple2::v2);
  }

}
