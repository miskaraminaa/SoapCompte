package ma.ensa.scompte

import android.os.Bundle
import android.widget.RadioButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ma.ensa.scompte.adapter.CompteAdapter
import ma.ensa.scompte.beans.Compte
import ma.ensa.scompte.beans.TypeCompte
import ma.ensa.scompte.ws.SoapService

class MainActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var fabAdd: FloatingActionButton
    private val adapter = CompteAdapter()
    private val soapService = SoapService()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        initViews()
        setupRecyclerView()
        setupListeners()
        loadComptes()
    }

    private fun initViews() {
        recyclerView = findViewById(R.id.recyclerView)
        fabAdd = findViewById(R.id.fabAdd)
    }

    private fun setupRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        adapter.onDeleteClick = { compte ->
            MaterialAlertDialogBuilder(this)
                .setTitle("Supprimer le compte")
                .setMessage("Voulez-vous vraiment supprimer ce compte ?")
                .setPositiveButton("Supprimer") { _, _ ->
                    compte.id?.let {
                        lifecycleScope.launch(Dispatchers.IO) {
                            val success = soapService.deleteCompte(it)
                            withContext(Dispatchers.Main) {
                                if (success) {
                                    adapter.removeCompte(compte)
                                    Toast.makeText(this@MainActivity, "Compte supprimé avec succès", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(this@MainActivity, "Compte supprimé avec succès", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }
                }
                .setNegativeButton("Annuler", null)
                .show()
        }

        // Ajout de la gestion du clic sur "Edit" pour modifier un compte
        adapter.onEditClick = { compte ->
            showEditCompteDialog(compte)
        }
    }

    private fun setupListeners() {
        fabAdd.setOnClickListener { showAddCompteDialog() }
    }

    private fun showAddCompteDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_account, null)

        MaterialAlertDialogBuilder(this)
            .setView(dialogView)
            .setTitle("Ajouter un compte bancaire")
            .setPositiveButton("Ajouter") { _, _ ->
                val etSolde = dialogView.findViewById<TextInputEditText>(R.id.etSolde)
                val radioCourant = dialogView.findViewById<RadioButton>(R.id.radioCourant)

                val solde = etSolde.text.toString().toDoubleOrNull() ?: 0.0
                val type = if (radioCourant.isChecked) TypeCompte.COURANT else TypeCompte.EPARGNE

                if (solde > 0) {
                    lifecycleScope.launch(Dispatchers.IO) {
                        val success = soapService.createCompte(solde, type)
                        withContext(Dispatchers.Main) {
                            if (success) {
                                loadComptes()
                                Toast.makeText(this@MainActivity, "Compte ajouté avec succès", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(this@MainActivity, "Erreur lors de l'ajout du compte", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                } else {
                    Toast.makeText(this@MainActivity, "Le solde doit être positif", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Annuler", null)
            .show()
    }

    private fun showEditCompteDialog(compte: Compte) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_edit_account, null)

        val etSolde = dialogView.findViewById<TextInputEditText>(R.id.etSolde)
        val radioCourant = dialogView.findViewById<RadioButton>(R.id.radioCourant)
        val radioEpargne = dialogView.findViewById<RadioButton>(R.id.radioEpargne)

        // Pré-remplir les champs avec les données actuelles du compte
        etSolde.setText(compte.solde.toString())
        if (compte.type == TypeCompte.COURANT) {
            radioCourant.isChecked = true
        } else {
            radioEpargne.isChecked = true
        }

        MaterialAlertDialogBuilder(this)
            .setView(dialogView)
            .setTitle("Modifier le compte bancaire")
            .setPositiveButton("Modifier") { _, _ ->
                val solde = etSolde.text.toString().toDoubleOrNull() ?: 0.0
                val type = if (radioCourant.isChecked) TypeCompte.COURANT else TypeCompte.EPARGNE

                if (solde > 0) {
                    lifecycleScope.launch(Dispatchers.IO) {
                        compte.id?.let {
                            val success = soapService.updateCompte(it, solde, type)
                            withContext(Dispatchers.Main) {
                                if (success) {
                                    loadComptes()
                                    Toast.makeText(this@MainActivity, "Compte modifié avec succès", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(this@MainActivity, "Erreur lors de la modification du compte", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }
                } else {
                    Toast.makeText(this@MainActivity, "Le solde doit être positif", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Annuler", null)
            .show()
    }

    private fun loadComptes() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val comptes = soapService.getComptes()
                withContext(Dispatchers.Main) {
                    if (comptes.isNotEmpty()) {
                        adapter.updateComptes(comptes)
                    } else {
                        Toast.makeText(this@MainActivity, "Aucun compte trouvé", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@MainActivity, "Erreur: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
