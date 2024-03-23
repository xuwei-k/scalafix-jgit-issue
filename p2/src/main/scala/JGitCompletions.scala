import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.Constants.DOT_GIT
import org.eclipse.jgit.lib.Ref
import org.eclipse.jgit.lib.RefDatabase
import org.eclipse.jgit.lib.Repository
import org.eclipse.jgit.lib.RepositoryCache
import org.eclipse.jgit.revwalk.RevCommit
import org.eclipse.jgit.storage.file.FileRepositoryBuilder
import org.eclipse.jgit.util.FS
import org.eclipse.jgit.util.GitDateFormatter

import java.nio.file.Path
import scala.collection.JavaConverters._
import scala.util.Try

class JGitCompletion(cwd: Path) {
  private val isGitRepository =
    RepositoryCache.FileKey
      .isGitRepository(cwd.resolve(DOT_GIT).toFile, FS.DETECTED)

  private case class RefValues(refList: Seq[Ref], refs: List[RevCommit])

  // don't change to val for avoid memory leak!!!
  private def refValues(): RefValues =
    if (isGitRepository) {
      val builder = new FileRepositoryBuilder()
      val repo = builder.readEnvironment().setWorkTree(cwd.toFile).build()
      val refList0 =
        repo.getRefDatabase().getRefsByPrefix(RefDatabase.ALL).asScala
      val git = new Git(repo)
      val refs0 =
        Try(git.log().call().asScala.toList).getOrElse(Nil)
      RefValues(refList0, refs0)
    } else {
      RefValues(Nil, Nil)
    }

  val branchesAndTags: List[String] =
    refValues().refList.map { ref => Repository.shortenRefName(ref.getName) }.toList

  private val dateFormatter = new GitDateFormatter(
    GitDateFormatter.Format.RELATIVE
  )
  val last20Commits: List[(String, String)] = {
    val refs = refValues().refs
    refs.map { ref =>
      val relativeCommitTime =
        dateFormatter.formatDate(refs.head.getCommitterIdent)
      val abrev = ref.abbreviate(8).name
      val short = ref.getShortMessage
      (s"$abrev -- $short ($relativeCommitTime)", abrev)
    }
  }
}
