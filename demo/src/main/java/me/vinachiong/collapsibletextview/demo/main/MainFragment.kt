package me.vinachiong.collapsibletextview.demo.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import me.vinachiong.collapsibletextview.CollapsibleTextView
import me.vinachiong.collapsibletextview.demo.R

class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment()
    }

    private lateinit var viewModel: MainViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        return inflater.inflate(R.layout.main_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        view!!.apply {
            val textView = this.findViewById<CollapsibleTextView>(R.id.ctv_1)

            textView.text = "[CollapsibleTextView] ==> 是否单行：${textView.isSingleLine}, \n最少显示${textView.minLines}行"

            textView.setOnClickListener {
                // 每次点击都修改ViewModel的值
                viewModel.currentName.value = System.currentTimeMillis().toString()
                textView.toggle()
            }

            viewModel.currentName.observe(viewLifecycleOwner, Observer<String> { newName ->
                // 监听ViewModel的值变化记录，添加入CollapsibleTextView
                textView.addContents("change value to :'$newName'")
                textView.setContents(listOf())
            })
        }
    }
}
