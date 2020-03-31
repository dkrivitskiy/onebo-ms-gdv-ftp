package com.one.gdvftp.entity;

import java.util.List;
import java.util.stream.Collectors;
import lombok.val;

public interface Display {

  /** returns a short string to display the entity */
  String display();

  static <T extends Display> String display(List<T> list) {
    val displays = list.stream().map(Display::display).collect(Collectors.toList());
    val s = String.join(", ", displays);
    return "["+s+"]";
  }

}
