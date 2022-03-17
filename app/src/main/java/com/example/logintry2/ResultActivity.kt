package com.example.logintry2

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.github.chrisbanes.photoview.PhotoView
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.mancj.materialsearchbar.MaterialSearchBar
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.nav_header.*
import org.w3c.dom.Text

class ResultActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    // 내비게이션 바
    lateinit var toolbar: Toolbar
    lateinit var drawerLayout: DrawerLayout
    lateinit var navView: NavigationView

    // @ 전달해줄 역명
    var subwayname= arrayOf("홍대입구", "사당", "건대입구", "성수", "낙성대")
    var detailsub=""

    // 파이어베이스 인증
    private lateinit var firebaseAuth: FirebaseAuth

    // 구글 클라이언트
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        // 검색바
        val lv=findViewById(R.id.metrolist) as ListView
        val searchBar=findViewById(R.id.MapSearchbar) as MaterialSearchBar
        searchBar.setHint("Search..")
        searchBar.setSpeechMode(true)

        // 검색 바 리스트에 어댑터 적용 ( 변경한 코드 )
        var adapter = MyCustomAdapter(this) //this needs to be my custom adapter telling my list what to render
        lv.adapter = adapter

        // 리스트뷰 클릭이벤트
        // 1/20 ★★ DetailsActivity에 호선이랑 역이름 값 전달해줘야할 부분!!!!!!!!
//        lv.setOnItemClickListener { parent:AdapterView<*>, view:View, position:Int, id:Long ->
//            if (position==0){
//                var DetailsIntent=Intent(this,DetailsActivity::class.java)
//                startActivity(DetailsIntent)
//            }
//        }

        // 검색바 적용
        MapSearchbar.addTextChangeListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Search Filter
//                adapter1.getFilter().filter(s) // 데이터베이스 검색 기능을 따로 구현해주어야 한다. 데이터베이스에 API 정보 올리기
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })

        // 리스트뷰 클릭이벤트
        // 1/20 ★★ DetailsActivity에 호선이랑 역이름 값 전달해줘야할 부분!!!!!!!!
        var DetailsIntent=Intent(this,DetailsActivity::class.java)

        lv.setOnItemClickListener(object : AdapterView.OnItemClickListener{
            override fun onItemClick(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                // 역명은
                if (position==0||position==1||position==2||position==3||position==4){
                    startActivity(DetailsIntent)
                }
            }
        })

        // 검색바 VISIBILITY
        MapSearchbar.setOnFocusChangeListener(object:View.OnFocusChangeListener{
            override fun onFocusChange(v: View?, hasFocus: Boolean) {
                if(!hasFocus==true){
                    lv.visibility=View.VISIBLE
                } else {
                    lv.visibility=View.GONE
                }
            }
        })

        // 지도 포토뷰
        val photoView:PhotoView=findViewById(R.id.metro_photoview)
        photoView.setImageResource(R.drawable.metro)

        // 내비게이션 바
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        getSupportActionBar()!!.setTitle(null) // 툴바 제목 없애기

        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)

        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar, 0, 0
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        navView.setNavigationItemSelectedListener(this)

        // 구글 로그인 옵션 구성 requestToken 및 Email 요청
        val gso= GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient= GoogleSignIn.getClient(this,gso)

        // 파이어베이스 인증 객체
        firebaseAuth=FirebaseAuth.getInstance()

    }

    // 내비게이션 바 메뉴 클릭 설정
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_metro1 -> {
                Toast.makeText(this, "1호선 클릭", Toast.LENGTH_SHORT).show()
            }
            R.id.nav_metro2 -> {
                Toast.makeText(this, "2호선 클릭", Toast.LENGTH_SHORT).show()
            }
            R.id.nav_metro3 -> {
                Toast.makeText(this, "3호선 클릭", Toast.LENGTH_SHORT).show()
            }
            R.id.nav_metro4 -> {
                Toast.makeText(this, "4호선 클릭", Toast.LENGTH_SHORT).show()
            }
            R.id.nav_metro5 -> {
                Toast.makeText(this, "5호선 클릭", Toast.LENGTH_SHORT).show()
            }
            R.id.nav_metro6 -> {
                Toast.makeText(this, "6호선 클릭", Toast.LENGTH_SHORT).show()
            }
            R.id.nav_metro7 -> {
                Toast.makeText(this, "7호선 클릭", Toast.LENGTH_SHORT).show()
            }
            R.id.nav_metro8 -> {
                Toast.makeText(this, "8호선 클릭", Toast.LENGTH_SHORT).show()
            }

            R.id.nav_setup -> {
                Toast.makeText(this, "설정", Toast.LENGTH_SHORT).show()
            }
            R.id.nav_logout -> {
                var main:MainActivity= MainActivity()
                if(main.loginEmailOrGoogle=="Google"){
                    signOutGoogle()
                    finish()
                }
                else if(main.loginEmailOrGoogle=="Email"){
                    signOutEmail()
                    main.loginEmailOrGoogle="Google"
                    finish()
                    client_name.text="" // 클라이언트 이름 초기화
                }
            }
            R.id.nav_bookmark->{
                Toast.makeText(this, "북마크", Toast.LENGTH_SHORT).show()
            }
            R.id.nav_filter->{
                val filterintent=Intent(this,FilterActivity::class.java)
                startActivity(filterintent)
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    // 이메일 로그아웃
    private fun signOutEmail() {
        FirebaseAuth.getInstance().signOut()
    }

    // 구글 로그아웃
    private fun signOutGoogle() {
        // Firebase sign out
        firebaseAuth.signOut()
        // Google sign out
        googleSignInClient.signOut().addOnCompleteListener(this){
            // updateUI(null)
        }
    }

    // 지하철 리스트뷰 어댑터 ( 변경 코드 )
    private class MyCustomAdapter(context: Context):BaseAdapter(){
        private val mContext:Context

        private val subnames=arrayListOf<String>(
            "홍대입구", "사당", "건대입구", "성수", "낙성대"
        ) // ★ 데이터베이스에서 역사명 가져와줄 부분

        init {
            mContext=context
        }
        // responsible for how many rows in my list
        override fun getCount(): Int {
            return subnames.size
        }

        // you can also ignore this
        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        // you can ignore this for now
        override fun getItem(position: Int): Any {
            val selecItem=subnames.get(position)
            return selecItem
        }

        // 출력되는 리스트 요소들 (Rendering)
        override fun getView(position: Int, convertView: View?, viewGroup: ViewGroup?): View {
            val layoutInflater= LayoutInflater.from(mContext)
            val row_sub=layoutInflater.inflate(R.layout.custom_listview,viewGroup,false)

            // 호선 이름
            val subwayname=row_sub.findViewById<TextView>(R.id.subway_name)
            subwayname.text=subnames.get(position)

            // 호선 사진
            val subwaynum_img=row_sub.findViewById<ImageView>(R.id.list_subimg)

            // ★ 반복문으로 구성하거나 역사명처럼 데이터베이스를 받아서 리스트로 만들어주기
            var subwaynum:String="2" // ★ 몇 호선인지를 데이터베이스로부터 가져와줄 부분
            Subimage(subwaynum,subwaynum_img) // 그에 맞는 이미지 파일 출력해주기
            return row_sub
        }

        // 검색기능 리스트뷰 : 호선 별 이미지 넣어주는 함수
        private fun Subimage(subnum:String,image:ImageView){
            when(subnum){
                "1"->
                    image.setImageResource(R.drawable.one)
                "2"->
                    image.setImageResource(R.drawable.two)
                "3"->
                    image.setImageResource(R.drawable.three)
//                "4"->
//                    image.setImageResource(R.drawable.four)
//                "5"->
//                    image.setImageResource(R.drawable.five)
//                "6"->
//                    image.setImageResource(R.drawable.six)
//                "7"->
//                    image.setImageResource(R.drawable.seven)
//                "8"->
//                    image.setImageResource(R.drawable.eight)
            }
        }
    }


}
