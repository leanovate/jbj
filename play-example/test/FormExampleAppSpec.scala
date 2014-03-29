import akka.util.Timeout
import java.util.concurrent.TimeUnit
import org.specs2.mutable.SpecificationWithJUnit
import play.api.http.{HttpProtocol, Status, HeaderNames}
import play.api.test._
import play.api.test.FakeApplication

class FormExampleAppSpec extends SpecificationWithJUnit with PlayRunners with HeaderNames with Status with HttpProtocol
with ResultExtractors with Writeables with RouteInvokers with WsTestClient with FutureAwaits with DefaultAwaitTimeout {
  override implicit def defaultAwaitTimeout: Timeout = Timeout(20, TimeUnit.SECONDS)

  "Formexample" should {
    "render the form on get" in {
      running(FakeApplication()) {
        val formexample = route(FakeRequest(GET, "/php/formexample.php")).get

        status(formexample) must equalTo(OK)
        contentType(formexample) must beSome.which(_ == "text/html")
        contentAsString(formexample) must contain("name=\"Fname\"") and contain("name=\"Lname\"")
      }
    }

    "render the result on post" in {
      running(FakeApplication()) {
        val formexample = route(FakeRequest(POST, "/php/formexample.php").withFormUrlEncodedBody(
          "Fname" -> "First", "Lname" -> "Last", "submit" -> "submit")).get

        status(formexample) must equalTo(OK)
        contentType(formexample) must beSome.which(_ == "text/html")
        contentAsString(formexample) must contain("Hello, First Last")
      }
    }
  }
}
