package com.example.logintry2

import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.AsyncTask
import android.os.Bundle
import android.os.PersistableBundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.MapFragment
import com.naver.maps.map.MapView
import com.naver.maps.map.NaverMap
import com.naver.maps.map.overlay.Marker
import kotlinx.android.synthetic.main.activity_details.*
import kotlinx.android.synthetic.main.activity_details.*
import org.json.JSONArray
import org.json.JSONObject
import java.net.URL

class DetailsActivity: AppCompatActivity() {
    // ★ 역을 입력받기
    val Result:ResultActivity= ResultActivity()

    // @ 전달받은 detailsub 변수
    var input_station="홍대입구"
    var input_line="2"

    // API
    val API_KEY="7574586b67646f723131334745575954"
    val API_KEY2="7549555a6c646f7236315371636545"
    var task:SubwayReadTask?=null // 앱이 비활성화될 때 백그라운드 작업도 취소하기 위한 변수 선언
    var stationcode_array= JSONArray() // 역 이름의 외부코드를 저장할 배열 변수
    var stationInfo_array= JSONArray()

    // 실험용
    var station_storage=Array<String>(10,{""})
    var stationInfo_storage=Array(10,{Array(10,{""})})

    // 역 정보를 읽어와 JSONObject로 반환하는 함수
    fun readData(startIndex:Int,lastIndex:Int,station_name:String): JSONObject {
        val url= URL("http://openAPI.seoul.go.kr:8088/${API_KEY}/json/SearchInfoBySubwayNameService/${startIndex}/${lastIndex}/${station_name}/")
        val connection=url.openConnection()

        val data=connection.getInputStream().readBytes().toString(charset("UTF-8"))
        return JSONObject(data)
    }

    fun readData2(startIndex2:Int, lastIndex2:Int, station_code:String): JSONObject {
        val url2= URL("http://openAPI.seoul.go.kr:8088/${API_KEY2}/json/SearchSTNInfoByFRCodeService/${startIndex2}/${lastIndex2}/${station_code}/")
        val connection2=url2.openConnection()

        val data2=connection2.getInputStream().readBytes().toString(charset("UTF-8"))
        return JSONObject(data2)
    }
    // 역 정보를 읽어오는 AsyncTask
    inner class SubwayReadTask: AsyncTask<Void, JSONArray, String>(){

        override fun onPreExecute() {
            stationcode_array= JSONArray()
            stationInfo_array= JSONArray()
        }

        override fun doInBackground(vararg params: Void?): String {
            // 서울시 데이터는 최대 1000개씩 가져올 수 있기 때문에
            // step만큼 startIndex와 lastIndex 값을 변경하며 여러번 호출해야함
            val step=1000
            var startIndex=1
            var lastIndex=step
            var totalCount=0

            do{
                // 백그라운드 작업이 취소된 경우 루프를 빠져나간다.
                if (isCancelled) break

                // totalCount가 0이 아닌 경우 최초 실행이 아니므로 step만큼 두 변수를 증가
                if (totalCount!=0) {
                    startIndex += step
                    lastIndex += step
                }

                // 세 변수로 데이터 조회
                val jsonObject=readData(startIndex,lastIndex,input_station)

                totalCount=jsonObject.getJSONObject("SearchInfoBySubwayNameService").getInt("list_total_count")
                // 역 코드 정보 집합을 가져온다
                var rows=jsonObject.getJSONObject("SearchInfoBySubwayNameService").getJSONArray("row")

                // 저장해주기
                stationcode_array.put(rows)
                var index=totalCount-1

                for(m in 0..index){
                    station_storage[m]=rows.getJSONObject(m).getString("FR_CODE")
                }

                // UI 업데이트를 위해 progress 진행
                publishProgress(rows)
            } while (lastIndex<totalCount) // lastIndex가 총 개수보다 적으면 반복한다


            // API(2)
            val step2=1000
            var startIndex2=1
            var lastIndex2=step2
            var totalCount2=0

            do{
                if(isCancelled) break

                if(totalCount2!=0){
                    startIndex2+=step2
                    lastIndex2+=step2
                }

                var index2=totalCount-1
                for(n in 0..index2){
                    var jsonObject2=readData2(startIndex2,lastIndex2,station_storage[n])

                    totalCount2=jsonObject2.getJSONObject("SearchSTNInfoByFRCodeService").getInt("list_total_count")
                    var rows2=jsonObject2.getJSONObject("SearchSTNInfoByFRCodeService").getJSONArray("row")

                    var line=rows2.getJSONObject(n).getString("LINE_NUM")

                    // 호선 저장
                    var linenum:Int=0
                    if(line=="2"){
                        linenum=2
                    }

                    stationInfo_storage[linenum][0]=rows2.getJSONObject(n).getString("STATION_NM")
                    stationInfo_storage[linenum][1]=rows2.getJSONObject(n).getString("LINE_NUM")
                    stationInfo_storage[linenum][2]=rows2.getJSONObject(n).getString("OBSTACLE")
                    stationInfo_storage[linenum][3]=rows2.getJSONObject(n).getString("BICYCLE")
                    stationInfo_storage[linenum][4]=rows2.getJSONObject(n).getString("MUIN")
                    stationInfo_storage[linenum][5]=rows2.getJSONObject(n).getString("NURSING")
                    stationInfo_storage[linenum][6]=rows2.getJSONObject(n).getString("WHEELCHAIR")
                    stationInfo_storage[linenum][7]=rows2.getJSONObject(n).getString("TOILET")
                    stationInfo_storage[linenum][8]=rows2.getJSONObject(n).getString("TEL")
                    stationInfo_storage[linenum][9]=rows2.getJSONObject(n).getString("ADDRESS")
                }
            } while(lastIndex2<totalCount2)
            return "complete"
        }

        override fun onProgressUpdate(vararg values: JSONArray?) {
            super.onProgressUpdate(*values)
            val array=values[0]
            array?.let {
                // 버튼 설정 : 자전거보관소
                var temp3=stationInfo_storage[input_line.toInt()][3]
                 if (temp3=="N"||temp3==""){
                    button7.setImageResource(R.drawable.nbike)
                }

                // 버튼 설정 : 수유실
                var temp5=stationInfo_storage[input_line.toInt()][5]
                if (temp5=="N"||temp5==""){
                    button6.setImageResource(R.drawable.nnursing)
                }

                // 버튼 설정 : 휠체어 리프트
                var temp6=stationInfo_storage[input_line.toInt()][6]
                if (temp6=="N"||temp6==""){
                    button8.setImageResource(R.drawable.nwheel)
                }

                // 버튼 설정 : 무인민원
                var temp4=stationInfo_storage[input_line.toInt()][4]
                if (temp4=="N"||temp4==""){
                    button9.setImageResource(R.drawable.nmuin)
                }

                // 화장실 개찰구
                textView5.text="화장실 "+stationInfo_storage[input_line.toInt()][7]

                // 전화번호 주소
                tel_tv.text="   전화번호 : "+stationInfo_storage[input_line.toInt()][8]
                add_tv.text="   주 소 : "+stationInfo_storage[input_line.toInt()][9]+"\n\n\n"

            }
        }
    }

    // 앱이 활성화될 때 서울시 데이터를 읽어옴
    override fun onStart() {
        super.onStart()
        task?.cancel(true)
        task=SubwayReadTask()
        task?.execute()
    }

    override fun onStop() {
        super.onStop()
        task?.cancel(true)
        task=null
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)

        // 네이버 지도 객체
//        mapView=findViewById(R.id.naver_mapView)
//        mapView.onCreate(savedInstanceState)

        // 앱바 : 역이름 & 호선
        tb_top.setTitle(input_station)
        line.text=input_line+"호선"

        // 앱바 : 색상 설정
        when(input_line){
            "1"->
                tb_top.setBackgroundColor(Color.rgb(0,30,201))
            "2"->
                tb_top.setBackgroundColor(Color.rgb(139,195,74))
            "3"->
                tb_top.setBackgroundColor(Color.rgb(237,169,0))
            "4"->
                tb_top.setBackgroundColor(Color.rgb(0,198,237))
            "5"->
                tb_top.setBackgroundColor(Color.rgb(113,18,255))
            "6"->
                tb_top.setBackgroundColor(Color.rgb(171,130,18))
            "7"->
                tb_top.setBackgroundColor(Color.rgb(156,129,54))
            "8"->
                tb_top.setBackgroundColor(Color.rgb(255,0,127))
        }

//        // 버튼 설정 : 장애인 시설
//        for(line in 0..station_storage.size){
//            if (stationInfo_storage[line][1]==input_line){
//                var temp=stationInfo_storage[line][2]
//                if(temp=="Y"){
//                    button5.setImageResource(R.drawable.obstacle)
//                } else if (temp=="N"||temp==""){
//                    button5.setImageResource(R.drawable.nobstcle)
//                }
//            }
//        }


        // 지도 누를시 확대
        map.setOnClickListener {
            val mapIntent=Intent(this,MapActivity::class.java)
            startActivity(mapIntent)
        }


        button1.setOnClickListener { _ ->
            var dialog = AlertDialog.Builder(this)
            dialog.setTitle("화장실")
            dialog.setMessage("\n화장실은 2번 출구에 있습니다.")

            fun toast_p() {
                Toast.makeText(this, "Positive 버튼", Toast.LENGTH_SHORT)

            }

            var dialog_listener = object : DialogInterface.OnClickListener {
                override fun onClick(dialog: DialogInterface?, which: Int) {
                    when (which) {
                        DialogInterface.BUTTON_POSITIVE ->
                            toast_p()

                    }
                }
            }
            dialog.setPositiveButton("확인완료", dialog_listener)

            dialog.show()
        }
//
        button2.setOnClickListener { _ ->
            var dialog = AlertDialog.Builder(this)
            dialog.setTitle("엘리베이터")
            dialog.setMessage("엘리베이터는 섬식(외) 7-2, 8번 출구 측에 있습니다.")

            fun toast_p() {
                Toast.makeText(this, "Positive  버튼", Toast.LENGTH_SHORT)
            }

            var dialog_listener = object : DialogInterface.OnClickListener {
                override fun onClick(dialog: DialogInterface?, which: Int) {
                    when (which) {
                        DialogInterface.BUTTON_POSITIVE ->
                            toast_p()
                    }
                }
            }
            dialog.setPositiveButton("확인완료", dialog_listener)

            dialog.show()
        }

        button3.setOnClickListener { _ ->
            var dialog = AlertDialog.Builder(this)
            dialog.setTitle("에스컬레이터")
            dialog.setMessage("에스컬레이터는 1번, 2번 출구에 있습니다.")

            fun toast_p() {
                Toast.makeText(this, "Positive  버튼", Toast.LENGTH_SHORT)
            }

            var dialog_listener = object : DialogInterface.OnClickListener {
                override fun onClick(dialog: DialogInterface?, which: Int) {
                    when (which) {
                        DialogInterface.BUTTON_POSITIVE ->
                            toast_p()
                    }
                }
            }
            dialog.setPositiveButton("확인완료", dialog_listener)

            dialog.show()
        }

        button4.setOnClickListener { _ ->
            var dialog = AlertDialog.Builder(this)
            dialog.setTitle("안내데스크")
            dialog.setMessage("안내데스크는 4번 출구에서 지상2층에서 오른쪽으로 25M 지점에 있습니다.")

            fun toast_p() {
                Toast.makeText(this, "Positive  버튼", Toast.LENGTH_SHORT)
            }

            var dialog_listener = object : DialogInterface.OnClickListener {
                override fun onClick(dialog: DialogInterface?, which: Int) {
                    when (which) {
                        DialogInterface.BUTTON_POSITIVE ->
                            toast_p()
                    }
                }
            }
            dialog.setPositiveButton("확인완료", dialog_listener)

            dialog.show()
        }

        button5.setOnClickListener { _ ->
            var dialog = AlertDialog.Builder(this)
            dialog.setTitle("물품보관소")
            dialog.setMessage("물품보관소는 게이트에서 1,9번(레드아이앞) 방향과 3,4번(화장실 옆) 방향에 있습니다.")

            fun toast_p() {
                Toast.makeText(this, "", Toast.LENGTH_SHORT)
            }

            var dialog_listener = object : DialogInterface.OnClickListener {
                override fun onClick(dialog: DialogInterface?, which: Int) {
                    when (which) {
                        DialogInterface.BUTTON_POSITIVE ->
                            toast_p()
                    }
                }
            }
            dialog.setPositiveButton("확인완료", dialog_listener)

            dialog.show()
        }
    }
}