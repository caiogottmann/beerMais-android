package br.com.joseneves.beerMais.android

import android.app.Dialog
import android.os.AsyncTask
import android.os.Bundle
import android.support.v4.app.DialogFragment
import br.com.joseneves.beerMais.android.Database.DAO.BeerDAO
import br.com.joseneves.beerMais.android.Database.Database
import br.com.joseneves.beerMais.android.Model.Beer
import kotlinx.android.synthetic.main.new_beer_modal.*

class NewBeer: DialogFragment() {
    private lateinit var newBeerDialog: Dialog
    private lateinit var beerDAO: BeerDAO
    private lateinit var beer: Beer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        newBeerDialog = Dialog(this.context!!)
        newBeerDialog.setContentView(R.layout.new_beer_modal)
        newBeerDialog.window.setBackgroundDrawableResource(android.R.color.transparent)

        val database = Database.instance(this.context!!)
        beerDAO = database.beerDAO()

        newBeerDialog.add_button.setOnClickListener {
            val beer = createBeer()
            if(beer != null) {
                this.beer = beer
                SaveBeer().execute()
                dismiss()
            }
        }

        return newBeerDialog
    }

    private fun createBeer(): Beer? {
        val brand = newBeerDialog.textInputLayoutBrand.editText?.text.toString()

        val valueString = newBeerDialog.textInputValue.editText?.text.toString()
        var value = 0.0f
        if (valueString.isNotEmpty()) {
            value = valueString.toFloat()
        }

        val amountString = newBeerDialog.textInputAmount.editText?.text.toString()
        var amount = 0
        if(amountString.isNotEmpty()) {
            amount = amountString.toInt()
        }

        var beer: Beer? = null
        if(isValidBeer(brand, value, amount)) {
            beer = Beer(amount = amount, brand = brand, type = 1, value = value)
        }

        return beer
    }

    private fun isValidBeer(brand: String, value: Float, amount: Int): Boolean {
        val isNotValidBrand = brand.isEmpty()
        val isNotValidValue = value < 0.01f
        val isNotValidAmount = amount < 1

        if(isNotValidBrand) {
            newBeerDialog.textInputLayoutBrand.error = getString(R.string.brandError)
        } else {
            newBeerDialog.textInputLayoutBrand.error = null
        }

        if(isNotValidValue) {
            newBeerDialog.textInputValue.error = getString(R.string.valueError)
        } else {
            newBeerDialog.textInputValue.error = null
        }

        if(isNotValidAmount) {
            newBeerDialog.textInputAmount.error = getString(R.string.amountError)
        } else {
            newBeerDialog.textInputAmount.error = null
        }

        return !isNotValidBrand && !isNotValidValue && !isNotValidAmount
    }

    inner class SaveBeer: AsyncTask<Void, Void, Void>() {
        override fun doInBackground(vararg p0: Void?): Void? {
            beerDAO.add(beer)

            return null
        }
    }
}