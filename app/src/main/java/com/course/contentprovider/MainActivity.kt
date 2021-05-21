package com.course.contentprovider

import android.database.Cursor
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.BaseColumns._ID
import androidx.loader.app.LoaderManager
import androidx.loader.content.CursorLoader
import androidx.loader.content.Loader
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.course.contentprovider.database.NotesDatabaseHelper.Companion.TITLE_NOTES
import com.course.contentprovider.database.NotesProvider.Companion.URI_NOTES
import com.google.android.material.floatingactionbutton.FloatingActionButton

//GIT ...https://github.com/EduardoTakeda/AULA-Desenvolvimento-Integrado-de-aplica-es-Android.git

//PARA PODER TRABALHAR COM Content PROVIDER, é preciso instanciar o LoaderManager
//LoaderManager faz buscas em segundo plano do Cursor, para evitar erros de thread na aplicação
class MainActivity : AppCompatActivity(), LoaderManager.LoaderCallbacks<Cursor> {

    lateinit var noteRecyclerView: RecyclerView
    lateinit var noteAdd: FloatingActionButton

    lateinit var adapter: NotesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        noteAdd= findViewById(R.id.note_add)
        noteAdd.setOnClickListener {
            NotesDetailFragment().show(supportFragmentManager, "dialog")
        }
        //noteRecyclerView=findViewById(R.id.notes_recycler)

        adapter= NotesAdapter(object : NoteClickedListener {
            override fun noteClickedItem(cursor: Cursor) {
                val id = cursor.getLong(cursor.getColumnIndex(_ID))
                val fragment:NotesDetailFragment= NotesDetailFragment.newInstance(id)
                fragment.show(supportFragmentManager, "dialog") //O fragmentManager só fica dentro da Activity

            }

            override fun noteRemoveItem(cursor: Cursor?) {
                val id= cursor?.getLong(cursor.getColumnIndex(_ID))
                contentResolver.delete(Uri.withAppendedPath(URI_NOTES, id.toString()), null, null)

            }

        })
        //setHasStableIds para que não tenha ids repetidos dentro do adapter
        adapter.setHasStableIds(true)

        noteRecyclerView= findViewById(R.id.notes_recycler)
        noteRecyclerView.layoutManager= LinearLayoutManager(this)
        noteRecyclerView.adapter= adapter

        LoaderManager.getInstance(this).initLoader(0,null, this)
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> =
        //instanciar aquilo que sera buscado
        CursorLoader(this, URI_NOTES, null, null, null, TITLE_NOTES)

    override fun onLoadFinished(loader: Loader<Cursor>, data: Cursor?) {
        //pegar os dados recebidos para manipular
        if (data != null) { adapter.setCursor(data) }
    }

    override fun onLoaderReset(loader: Loader<Cursor>) {
        adapter.setCursor(null)
        //acabar com a pesquisa em segundo plano do LoadManager
    }
} //android:src="@drawable/ic_land3"