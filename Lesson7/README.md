 # Lession7 Storing Data in SQLite 정리

 1. SQLite의 CRUD와 데이터 저장 방식을 설명

 2. 'contract' 에 대해 배운다. 테이블과 테이블 column에 대한 정보를 가지고 있다.

3. contract 코딩하기
 - Contract 클래스를 만들고 이너 클래스로 BaseColumns를 implement한 클래스를 만든다. 자동으로PK \_ID를 연결 시켜준다. 늘 만들어야 하는건 아니다.

4. UnitTest를 배운다. contract를 제대로 implement 했는지 알 수 있다. 패키지(test)directory의 class를 우클릭하여 실행 가능하다.

5. Contract를 마저 만들면 BaseColumns를 implemet한 클래스에 table이름, column이름들을 만든다.

6. DBhelper를 만들어야 한다. DB처음 연결이나 SCHEMA가 바뀔때 업그레이드 하는데 쓰인다. DBHelper는 다른 클래스가 데이터베이스에 접근하게 도와준다. Helper에 OnCreate 는 실제 데이터베이스를 만드는데 쓰이고 onUpgrade는 schema가 바뀔때 업그레이드를 위해 쓰인다.

7. Helper 코드
~~~
public class WaitlistDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "waitlist.db";  //데이터베이스 이름
    private static final int DATABASE_VERSION = 1;              //데이터베이스 버젼 처음은 꼭 1이고 후에 업데이트마다 카운트를 올려주어야 한다.
    //생성자는 super로 parnet's constructor 를 사용한다.
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase){ //데이터베이스 처음 만드는 코드이다.
        final String SQL_CREATE_WAITLIST_TABLE = "CREATE_TABLE" +
        WaitlistEntry.TABLE_NAME + "(" + // 이런 식으로 쿼리를 만들어 나간다.
        WaitlistEntry._ID + "INTEGER PRIMARY KEY AUTOINCREMENT," +
        WaitlistEntry.COLUMN_GUEST_NAME + " TEXT NOT NULL, " +
        WaitlistEntry.COLUMN_PARTY_SIZE + "INTEGER NOT NULL, " +
        WaitlistEntry.COLUMN_TIMESTAMP + "TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
        ");";
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS" + WaitlistEntry.TABLE_NAME);
        onCreate(db);

    }

}
~~~

8. 검색하기, fakeData집어넣기, Cursor은 SQLquery 결과가 저장되는 곳으로 반복 검색을 용이하게 한다.
~~~
in onCreate
WaitlistDbHelper dbHelper = new WaitlistDbHelper(this);
mOb = dbHelper.getWritableDatabase();// 만약 읽기만 할거라면 dbHelper.getReadableDatabase();

private Cursor getAllGuests(){
    return mOb.query{
        WaitlistContract.WaitlistEntry.TABLE_NAME,// 테이블 이름
        null, //PROJECTION ARRAY- 관심있는 COLUMN 지정
        null, null, null, null,
        WaitlistContract.WaitlistEntry.COLUMN_TIMESTAMP //ORDERBY
    }
}

now Wecan get retrieve result. return value is Cursor
Cursor cursor = getAllGuests();
~~~

9. Cursor 사용법을 배운다. moveToPosition으로 원하는 row를 고를 수 있다. 만약 갈 수 있는 row일 경우 false를 반환한다.
원하는 row 후 column을 고르기 위해서는 cursor.get자료형(cursor.getColumnIndex(COLUMN_NAME))을 해야한다.

10. ContenValues 사용, key value로 row를 담을 수 있으며 데이터베이스에 삽입할 때 사용한다.
~~~
db.insert(String 테이블이름, String nullColumnHack, ContentView ContentView);//이렇게 사용
~~~
테이블이 업데이트되어 이전 커서가 필요 없을 때는 cursor.close()를 해 준다.

11. 데이터베이스 제거를 배운다. 두번째 인자는 row를 지정하고 세번재는 복잡한 연산을 할 때 필요하다. 지워진 row개수만큼 return한다.
~~~
db.delete(String 테이블이름, String whereClause, String[] whereArgs);
~~~
12. ItemTouchHelper 에 대해서 배운다.
~~~
new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT){
    @Override
    public boolean onMove{
        return false;
    }
    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction){

    }
});//  0은 드래그를 의미한다.
~~~
RecyclerView 에서 필요없는 정보지만 저장해 두기 위해서
holder.itemView.setTag();를 제공한다.
