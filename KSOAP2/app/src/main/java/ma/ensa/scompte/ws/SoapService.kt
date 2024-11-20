package ma.ensa.scompte.ws

import android.util.Log
import ma.ensa.scompte.beans.Compte
import ma.ensa.scompte.beans.TypeCompte
import org.ksoap2.SoapEnvelope
import org.ksoap2.serialization.SoapObject
import org.ksoap2.serialization.SoapSerializationEnvelope
import org.ksoap2.transport.HttpTransportSE
import java.text.SimpleDateFormat
import java.util.*


class SoapService {

    private val NAMESPACE = "http://ws.scompte.ensa.ma/"
    private val URL = "http://10.0.2.2:8082/services/ws"
    private val METHOD_CREATE_COMPTE = "createCompte"
    private val METHOD_DELETE_COMPTE = "deleteCompte"

    private val SOAP_ACTION = ""

    @Throws(Exception::class)
    fun getComptes(): List<Compte> {
        val request = SoapObject("http://ws.scompte.ensa.ma/", "getComptes")
        val envelope = SoapSerializationEnvelope(SoapEnvelope.VER11)
        envelope.dotNet = true
        envelope.setOutputSoapObject(request)

        val transport = HttpTransportSE(URL)
        transport.debug = true
        transport.call(null, envelope)

        val response = envelope.bodyIn as SoapObject
        val comptes: MutableList<Compte> = mutableListOf()

        for (i in 0 until response.propertyCount) {
            val soapCompte = response.getProperty(i) as SoapObject
            val compte = Compte(
                id = soapCompte.getPropertySafely("id").toString().toLongOrNull(),
                solde = soapCompte.getPropertySafely("solde").toString().toDoubleOrNull() ?: 0.0,
                dateCreation = Date(), // You might want to parse the actual date from the SOAP response
                type = TypeCompte.valueOf(soapCompte.getPropertySafely("type").toString())
            )
            comptes.add(compte)
        }

        return comptes
    }


    fun createCompte(solde: Double, type: TypeCompte): Boolean {
        val request = SoapObject(NAMESPACE, METHOD_CREATE_COMPTE)
        request.addProperty("solde", solde.toString())
        request.addProperty("type", type.name)

        val envelope = SoapSerializationEnvelope(SoapEnvelope.VER11)
        envelope.dotNet = false
        envelope.setOutputSoapObject(request)

        return try {
            val transport = HttpTransportSE(URL)
            transport.debug = true
            transport.call(SOAP_ACTION, envelope)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
    fun updateCompte(id: Long, solde: Double, type: TypeCompte): Boolean {
        val request = SoapObject(NAMESPACE, "updateCompte")  // Méthode SOAP pour la modification
        request.addProperty("id", id.toString())
        request.addProperty("solde", solde.toString())
        request.addProperty("type", type.name)

        val envelope = SoapSerializationEnvelope(SoapEnvelope.VER11)
        envelope.dotNet = false
        envelope.setOutputSoapObject(request)

        return try {
            val transport = HttpTransportSE(URL)
            transport.debug = true
            transport.call(SOAP_ACTION, envelope)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun deleteCompte(id: Long): Boolean {
        val request = SoapObject(NAMESPACE, METHOD_DELETE_COMPTE)
        request.addProperty("id", id.toString())

        val envelope = SoapSerializationEnvelope(SoapEnvelope.VER11)
        envelope.dotNet = false
        envelope.setOutputSoapObject(request)

        return try {
            val transport = HttpTransportSE(URL)
            transport.debug = true
            transport.call(SOAP_ACTION, envelope)  // Assure-toi que SOAP_ACTION_DELETE est bien défini

            // Vérifie la réponse du serveur
            val response = envelope.response
            if (response is Boolean) {
                return response // Si la réponse est un Boolean, renvoie la valeur
            } else {
                // Log de la réponse du serveur pour mieux comprendre ce qui est retourné
                Log.e("SOAP", "Réponse inattendue : ${response.toString()}")
                return false
            }
        } catch (e: Exception) {
            // Log de l'exception pour aider à déboguer
            Log.e("SOAP", "Erreur lors de la suppression du compte : ${e.message}")
            e.printStackTrace()
            return false
        }
    }





}
