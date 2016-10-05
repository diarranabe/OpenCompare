package controllers

import javax.inject._

import com.mohiva.play.silhouette.api.{Environment, Silhouette}
import com.mohiva.play.silhouette.impl.authenticators.CookieAuthenticator
import models.{Database, User, Feedback}
import models.services.FeedbackService
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, Controller}
import play.api.libs.json.Json
import forms.FeedbackForm

@Singleton
class Application @Inject() (
    val messagesApi: MessagesApi,
    val env: Environment[User, CookieAuthenticator],
    feedbackService: FeedbackService)
    extends BaseController {

    def index = UserAwareAction { implicit request =>
        Ok(views.html.index(Database.getLastHTMLSources(5)))
    }

    def aboutProject = UserAwareAction { implicit request =>
        Ok(views.html.aboutProject())
    }

    def aboutPrivacyPolicy = UserAwareAction { implicit request =>
        Ok(views.html.aboutPrivacyPolicy())
    }

    def feedback = UserAwareAction { implicit request =>
        val json = Json.obj()
        FeedbackForm.form.bindFromRequest.fold(
            form => Ok(Json.obj("error" -> true)),
            data => {
                val feedback = Feedback(email = data.email, subject = data.subject, content = data.content)
                feedbackService.save(feedback)
                Ok(Json.obj("error" -> false))
            }
        )
    }

    def list(limit : Int, page : Int) = UserAwareAction { implicit request =>
        val pcms = Database.list(limit, page).toList
        val count = Database.count().toInt
        var nbPages = count / limit
        if (count % limit != 0) {
            nbPages = nbPages + 1
        }
        Ok(views.html.list(pcms, limit, page, nbPages))
    }

    def search(searchedString : String) = UserAwareAction { implicit request =>

        // TODO : find PCMs named "request" or with a product named "request"
        val results = Database.search(searchedString).toList

        Ok(views.html.search(searchedString, results))
    }


    def edit(id : String) = UserAwareAction { implicit request =>
        val exists = Database.exists(id)
        if (exists) {
            Ok(views.html.edit(id, null, null))
        } else {
            Ok(views.html.edit(null, null, null))
        }

    }

    def create = UserAwareAction { implicit request =>
        Ok(views.html.edit(null, null, null, true))
    }

    def importer(ext : String) = UserAwareAction { implicit request =>
        ext match {
            case "csv" => Ok(views.html.edit(null, null, "Csv"))
            case "html" => Ok(views.html.edit(null, null, "Html"))
            case "wikipedia" => Ok(views.html.edit(null, null, "MediaWiki"))
            case _ => NotFound
        }
    }

    def embed(id : String) = UserAwareAction { implicit request =>
        val exists = Database.exists(id)
        if (exists) {
            Ok(views.html.embed(id, null, null))
        } else {
            Ok(views.html.embed(null, null, null))
        }

    }

}
