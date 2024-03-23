## p1: sbt-scalafix 0.12.0 implementation

- https://github.com/xuwei-k/scalafix-jgit-issue/actions/runs/8401159730/job/23009236167#step:6:81
- https://github.com/scalacenter/sbt-scalafix/blob/v0.12.0/src/main/scala/scalafix/internal/sbt/JGitCompletions.scala

```
[info] (10:26:28.049017729,2000)
[info] (10:26:30.822149099,2050)
[info] (10:26:33.930797897,2100)
[info] (10:26:37.070750592,2150)
[info] (10:26:40.355611271,2200)
[info] (10:26:44.217524688,2250)
[info] (10:26:48.762974348,2300)
[info] (10:26:54.793003584,2350)
[info] (10:27:00.955814268,2400)
[info] (10:27:06.534912775,2450)
[info] (10:27:12.467903052,2500)
[info] (10:27:16.268383759,2550)
[info] (10:27:18.553224253,2600)
[info] (10:27:19.245838192,2650)
[error] Exception in thread "main" java.lang.OutOfMemoryError: Java heap space
[error] 	at org.eclipse.jgit.internal.storage.file.PackIndexV2.<init>(PackIndexV2.java:101)
[error] 	at org.eclipse.jgit.internal.storage.file.PackIndex.read(PackIndex.java:101)
[error] 	at org.eclipse.jgit.internal.storage.file.PackIndex.open(PackIndex.java:67)
[error] 	at org.eclipse.jgit.internal.storage.file.Pack.idx(Pack.java:161)
[error] 	at org.eclipse.jgit.internal.storage.file.Pack.get(Pack.java:273)
[error] 	at org.eclipse.jgit.internal.storage.file.PackDirectory.open(PackDirectory.java:216)
[error] 	at org.eclipse.jgit.internal.storage.file.ObjectDirectory.openPackedObject(ObjectDirectory.java:393)
[error] 	at org.eclipse.jgit.internal.storage.file.ObjectDirectory.openPackedFromSelfOrAlternate(ObjectDirectory.java:356)
[error] 	at org.eclipse.jgit.internal.storage.file.ObjectDirectory.openObjectWithoutRestoring(ObjectDirectory.java:346)
[error] 	at org.eclipse.jgit.internal.storage.file.ObjectDirectory.openObject(ObjectDirectory.java:331)
[error] 	at org.eclipse.jgit.internal.storage.file.WindowCursor.open(WindowCursor.java:132)
[error] 	at org.eclipse.jgit.revwalk.RevWalk.getCachedBytes(RevWalk.java:1119)
[error] 	at org.eclipse.jgit.revwalk.RevCommit.parseHeaders(RevCommit.java:126)
[error] 	at org.eclipse.jgit.revwalk.RevWalk.markStart(RevWalk.java:308)
[error] 	at org.eclipse.jgit.api.LogCommand.add(LogCommand.java:343)
[error] 	at org.eclipse.jgit.api.LogCommand.add(LogCommand.java:180)
[error] 	at org.eclipse.jgit.api.LogCommand.call(LogCommand.java:133)
[error] 	at JGitCompletion.$anonfun$x$1$1(JGitCompletions.scala:27)
[error] 	at JGitCompletion$$Lambda$36/0x00007f5dc4102660.apply(Unknown Source)
[error] 	at scala.util.Try$.apply(Try.scala:213)
[error] 	at JGitCompletion.<init>(JGitCompletions.scala:27)
[error] 	at Main$.$anonfun$main$1(Main.scala:10)
[error] 	at Main$.$anonfun$main$1$adapted(Main.scala:6)
[error] 	at Main$$$Lambda$1/0x00007f5dc400ee80.apply(Unknown Source)
[error] 	at scala.collection.generic.GenTraversableFactory.tabulate(GenTraversableFactory.scala:150)
[error] 	at Main$.main(Main.scala:6)
[error] 	at Main.main(Main.scala)
```


## p2: avoid retain jgit objects

```diff
> import org.eclipse.jgit.lib.Ref
5a7
> import org.eclipse.jgit.revwalk.RevCommit
19c21,24
<   private val (refList, refs) =
---
>   private case class RefValues(refList: Seq[Ref], refs: List[RevCommit])
> 
>   // don't change to val for avoid memory leak!!!
>   private def refValues(): RefValues =
28c33
<       (refList0, refs0)
---
>       RefValues(refList0, refs0)
30c35
<       (Nil, Nil)
---
>       RefValues(Nil, Nil)
34c39
<     refList.map { ref => Repository.shortenRefName(ref.getName) }.toList
---
>     refValues().refList.map { ref => Repository.shortenRefName(ref.getName) }.toList
39c44,45
<   val last20Commits: List[(String, String)] =
---
>   val last20Commits: List[(String, String)] = {
>     val refs = refValues().refs
46a53
>   }
```

```
[info] (10:32:35.000087342,9550)
[info] (10:32:38.763450813,9600)
[info] (10:32:42.498787428,9650)
[info] (10:32:46.375248937,9700)
[info] (10:32:50.115403572,9750)
[info] (10:32:54.032573273,9800)
[info] (10:32:57.930056862,9850)
[info] (10:33:01.854402696,9900)
[info] (10:33:05.765992257,9950)
[success] Total time: 454 s (07:34), completed Mar 23, 2024, 10:33:09 AM
```
