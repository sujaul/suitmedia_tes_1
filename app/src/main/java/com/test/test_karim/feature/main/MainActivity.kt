package com.test.test_karim.feature.main

import android.os.Bundle
import androidx.navigation.findNavController
import com.galee.core.BaseActivity
import com.test.test_karim.R
import com.test.test_karim.util.gone
import com.test.test_karim.util.visible
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import org.koin.core.component.KoinComponent
import kotlin.coroutines.CoroutineContext

class MainActivity : BaseActivity(), CoroutineScope, KoinComponent {

    companion object{
        const val TAG = "MainActivity"
    }

    private val navController by lazy {
        findNavController(R.id.nav_host_fragment)
    }

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + Job()

    override fun getTagName(): String = TAG

    override fun layoutId(): Int = R.layout.activity_main

    override fun onCreateUI(savedInstanceState: Bundle?) {
        init(savedInstanceState)
    }

    override fun onAllPermissionGranted(level: Int) {
        super.onAllPermissionGranted(level)
    }

    override fun onDenyPermission(level: Int) {
        super.onDenyPermission(level)
        rationaleCallback()
    }

    private fun init(savedInstanceState: Bundle?){
        val navInflater = navController.navInflater
        val graph = navInflater.inflate(R.navigation.mobile_navigation)
        if (savedInstanceState == null) graph.setStartDestination(R.id.nav_first);
        navController.setGraph(graph);
    }

    override fun onSupportNavigateUp(): Boolean {
        return /*navController.navigateUp(appBarConfiguration) ||*/ super.onSupportNavigateUp()
    }

    fun setTittel(tittle: String, isVisible: Boolean = true){
        title_bar?.text = tittle
        if (isVisible)
            appbar?.visible()
        else
            appbar?.gone()
    }
}