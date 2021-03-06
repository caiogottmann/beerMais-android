package br.com.joseneves.beerMais.android.Home

import android.graphics.Rect
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.arch.lifecycle.Observer
import br.com.joseneves.beerMais.android.BeerAdapter
import br.com.joseneves.beerMais.android.Database.DAO.BeerDAO
import br.com.joseneves.beerMais.android.Database.Database
import br.com.joseneves.beerMais.android.Model.Beer
import br.com.joseneves.beerMais.android.NewBeer
import br.com.joseneves.beerMais.android.R
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.app_bar_home.*
import kotlinx.android.synthetic.main.content_home.*

class HomeActivity: AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, HomeContract.View {
    private var beerRecyclerView: RecyclerView? = null
    private var mAdapter: RecyclerView.Adapter<*>? = null

    private lateinit var beerDAO: BeerDAO
    private lateinit var presenter: HomeContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        setSupportActionBar(toolbar)

        setPresenter(HomePresenter(this))

        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)

        val database = Database.instance(this)
        beerDAO = database.beerDAO()
        val productsLiveData = beerDAO.all()
        productsLiveData.observe(this, Observer { beers ->
            beers?.let {
                presenter.calcRank(beers)

                mAdapter = BeerAdapter(beers)
                beerRecyclerView!!.adapter = mAdapter
            }
        })

        beerRecyclerView = findViewById(R.id.beer_recyclerView)
        beerRecyclerView!!.layoutManager = GridLayoutManager(this, 2)
        beerRecyclerView!!.addItemDecoration(RecyclerViewMargin())
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.home, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_settings -> {
                this.createBeer()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_about -> {
                // Handle the camera action
            }
            R.id.nav_help -> {

            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun setRank(beer: Beer, economy: String) {
        textViewBrand.text = beer.brand
        textViewValue.text = "R$ " + beer.value.toString()

        var amountText = beer.amount.toString() + " ml"

        if (beer.amount >= 1000) {
            amountText = "1 L"

            if (beer.amount >= 1010) {
                amountText = String.format("%.2f", (beer.amount.toFloat()/1000)).replace(".",",") + " L"
            }
        }

        textViewAmount.text = amountText
        textViewEconomy.text = economy
    }

    override fun onDestroy() {
        presenter.onDestroy()
        super.onDestroy()
    }

    override fun setPresenter(presenter: HomeContract.Presenter) {
        this.presenter = presenter
    }

    private fun createBeer() {
        NewBeer().show(supportFragmentManager,"new_beer_modal")
    }

    inner class RecyclerViewMargin: RecyclerView.ItemDecoration() {
        override fun getItemOffsets(
            outRect: Rect, view: View,
            parent: RecyclerView, state: RecyclerView.State
        ) {
            val totalWidth = parent.width
            val cardWidth = 150

            var sidePadding = (totalWidth - cardWidth) / 14
            sidePadding = Math.max(0, sidePadding)
            outRect.set(sidePadding, 0, 0, 0)
        }
    }
}
