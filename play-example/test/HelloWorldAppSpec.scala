import org.specs2.mutable.SpecificationWithJUnit
import play.api.test.Helpers._
import play.api.test.{FakeApplication, FakeRequest, WithApplication}

class HelloWorldAppSpec extends SpecificationWithJUnit {

  "Hello world" should {
    "render a simple php page" in {
      running(FakeApplication()) {
        val helloWorld = route(FakeRequest(GET, "/php/helloworld.php")).get

        status(helloWorld) must equalTo(OK)
        contentType(helloWorld) must beSome.which(_ == "text/html")
        contentAsString(helloWorld) must contain("Hello World")
      }
    }
  }

}
