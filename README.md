# CollapsibleTextView
CollapsibleTextView is a custom view on Android platform.
- Accept list of text as content of the view. Display them in a Collapsible height which depends on property to specific min lines. 
- toggle() function to show a whole content or min lines.
- Either choose mode `single line` which default truncate END ellipsize effect, or `break text` which auto wrapped break text for per content. 

# 背景（Background）：
受业务需求驱使，需要在一个区域内展示多行文本。最简单的方法通过添加`\n`换行符，在一个TextView内实现。
但不能满足`自动换行`或`超长省略`的需求。当然可以使用ListView或者RecyclerView实现，但未免太小材大用了。
通过自定义View，实现参数化绘制多行文本。支持对每行内容设置字体大小、颜色，及`单行超长省略/换行`。

## 主要功能（Features）：
1. 内容设置字体大小、颜色。
2. 可指定最少显示行数，默认最多显示5行；
3. 提供`toggle()`方法，切换显示全部或最少行数。
4. 文字不足最大限制行数时，可选择：
    - `超长截断文字`，默认值`TruncateAt.END`；
    - `超长换行`，不截断文字。

## 限制（Limitation）：
由于是普通的及定义View，缺失TextView已的一些功能，需要自行实现。

## 待添加、优化功能（To be continuing）：
1. 提供默认的"展开、收起"文本点击按钮，并开放定制
2. "单行模式"下，省略模式需要支持"START、MIDDLE"
2. 引入Spanned相关接口和实现类，为文本内容提供字符、段落级别的处理
3. 尝试利用`android.text.Layout`及其实现类进行优化
4. 展开、折叠的动画效果
5. Others


## 使用方法：
有两种方法设置文字：
(1)代码配置参数，设置文本内容
```kotlin
// 是否单行显示，即是否换行
ctv.isSingleLine = true
// 设置折叠时候，最少显示行数
ctv.minLines = 5 
//设置contents内容
val contents = listOf("a", "b")
ctv.setContent(contents)
```
(2)在xml中直接设置文字
```xml
<me.vinachiong.collapsibletextview.CollapsibleTextView
                android:id="@+id/etv"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:text="@string/long_poem" />
```
(3)可配置的属性有如下几项
```xml
<declare-styleable  name="CollapsibleTextView">
    <attr name="textColor" format="reference|color"/>
    <attr name="textSize" format="dimension|float"/>
    <attr name="android:text"/>
</declare-styleable >
```

## 实现原理：


## 感谢
#### [ExpandableTextView][1]
---------------------


[1]: https://github.com/Carbs0126/ExpandableTextView
