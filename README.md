# JUC
1. CompletableFuture：继承Future,扩展，思想可完全替换FutureTask
```$xslt
public class CompletableFuture<T> implements Future<T>, CompletionStage<T>
```
2. 主线程不要立刻结束，否则CompletableFuture使用的线程池会立刻关闭
