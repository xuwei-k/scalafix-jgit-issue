import java.io.File

object Main {
  def main(args: Array[String]): Unit = {
    val dir = new File("scala2").toPath
    List.tabulate(10000) { n =>
      if (n % 50 == 0) {
        println((java.time.LocalTime.now(), n))
      }
      new JGitCompletion(dir)
    }
  }
}

