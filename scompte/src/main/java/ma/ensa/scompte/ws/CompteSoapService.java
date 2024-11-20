package ma.ensa.scompte.ws;


import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;
import ma.ensa.scompte.entities.Compte;
import ma.ensa.scompte.entities.TypeCompte;
import ma.ensa.scompte.repositories.CompteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
@WebService(serviceName = "BanqueWS")
public class CompteSoapService {

    @Autowired
    private CompteRepository compteRepository;

    @WebMethod
    public List<Compte> getComptes() {
        return compteRepository.findAll();
    }

    @WebMethod
    public Compte getCompteById(@WebParam(name = "id") Long id) {
        return compteRepository.findById(id).orElse(null);
    }

    @WebMethod
    public Compte createCompte(@WebParam(name = "solde") double solde,
                               @WebParam(name = "type") TypeCompte type) {
        Compte compte = new Compte(null, solde, new Date(), type);
        return compteRepository.save(compte);
    }
    @WebMethod
    public Compte updateCompte(@WebParam(name = "id") Long id,
                               @WebParam(name = "solde") double solde,
                               @WebParam(name = "type") TypeCompte type) {
        // Vérifie si le compte existe déjà
        Compte compte = compteRepository.findById(id).orElse(null);

        if (compte != null) {
            // Si le compte existe, mets à jour ses informations
            compte.setSolde(solde);
            compte.setType(type);
            // Met à jour la date si nécessaire (par exemple, la dernière mise à jour)
            compte.setDateCreation(new Date());  // Optionnel, ou remplace par un autre champ de date si tu en as un pour la mise à jour

            // Sauvegarde le compte mis à jour
            return compteRepository.save(compte);
        }
        // Si le compte n'existe pas, retourne null ou tu peux lever une exception
        return null;
    }
    @WebMethod
    public boolean deleteCompte(@WebParam(name = "id") Long id) {
        if (compteRepository.existsById(id)) {
            compteRepository.deleteById(id);
            return true;
        }
        return false;
    }
}