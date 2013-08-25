import org.specs2.mutable.SpecificationWithJUnit
import play.api.test.Helpers._
import play.api.test.{FakeApplication, FakeRequest, WithApplication}

class FormExampleAppSpec extends SpecificationWithJUnit {
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
