package ca.llamabagel.elemetronome

import android.media.ToneGenerator
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

class ToneGeneratorFragment : Fragment() {
    companion object {
        fun newInstance(text: String): ToneGeneratorFragment {
            val m = ToneGeneratorFragment()
            val b = Bundle()
            b.putString("msg", text)
            m.arguments = b

            return m
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater!!.inflate(R.layout.fragment_tone_generator, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {}

}
