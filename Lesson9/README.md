# Lesson9 Building a Content Provider
1. manifest에 Provider 등록한다.
~~~
    <provider
        android:authorities="패키지이름"
        android:name="패키지이름+고유이름"
        android:exported="true or false" // true면 다른 앱에서 쓸 수 있고 false이면 못쓴다.
    />
~~~
2. Provider 의 onCreate에 DBHelper를 생성하여멤벼변수로 저장한다.
3. Provider에서 2가지 Uri를 만들어야 한다.
    - a. identify your provider (id)
    - b. different type of data that provider can work with // uri의 경로에 따라서 여러 다른 데이터에 접근 할 수 있다.
    - authority 뒤의 path 에 # 를 추가하면 아무 숫자, \*는 아무 string을 받는다. 이런 문자를 이용하여 num/#/name/* 등 더 원하는 정보를 전달 할 수 있다.
4. Contract안에 static값으로 String, Uri들을 만든다.
~~~
    public static final String AUTHORITY = "com.example.android.todolist";  //AUTHORITY, PATH, SCHEME 위주로 저장한다.
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);
    public static final String PATH_TASKS = "tasks";

    public static final class TaskEntry implements BaseColumns{ //바로가져다 쓸 수 있게 더 다양한 데이터에 접근하는 Uri들을 만들어야 한다
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_TASKS).build();
    }
~~~
5. 요청받은 Uri가 어떤 요청인지 알기 쉽게 하기 위해 UriMatcher 사용
~~~
    UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);//처음 빈 매쳐 생성

    uriMatcher.addURI(AUTHORITY, PATH, 호출될 int값);
    uriMatcher.addURI(AUTHORITY, PATH + "/#", 호출될 다른 int값); // 이런식으로 계속 늘려나갈 수 있다.
~~~
6. Resolver to Database Flow
~~~
    1. UI에서 Resolver를 불러 query를 요청한다.
    2. Resolver는 query의 authority로 원하는 provider를 찾는다.
    3. URIMatcher가 어떻게 반응해야할지 정한다.
    4. 정확한 SQL query로 바뀐다.
    5. database에 원하는 작업을 한뒤 다시 UI로 돌아온다.
~~~
7. Provider 메소드들
~~~
    CRUD관련 (insert query update delete)
    1. insert(Uri uri, ContentValues values); // 실패 -1 성공 row번호
    2. query(Uri uri,
        String[] projection,
        String selection,
        String[] selectionArgs,
        String sortOrder);
    3. update(Uri uri,
        ContentValues values,
        String selection,
        String[] selectionArgs);
    4. delete(Uri uri,
        String selection,
        String[] selectionArgs);
    5. getType(Uri uri);
~~~
8. insert만들기
~~~
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        final SQLiteDatabase db = mTaskDbHelper.getWritableDatabase();//db를 writable로 찾는다
        int match = sUriMatcher.match(uri);//UriMatcher로 원하는 Uri가 뭔지 찾는다
        Uri returnUri; // URI to be returned

        switch (match) {// 원하는 패쓰 사용 위해 switch 사용
            case TASKS:
                long id = db.insert(TABLE_NAME, null, values); //db에 넣는다.
                if ( id > 0 ) { //실패하면 -1 성공시 row값 반환한다.
                    returnUri = ContentUris.withAppendedId(TaskContract.TaskEntry.CONTENT_URI, id); // ContentUri.withAppendedId 는 원 uri에 패쓰를 더하는 기본제공 함수이다
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);//바꾸었으면 알려야지

        return returnUri;
    }
~~~

9. activity 에서 insert 요청보내기
~~~
    ContentValues contentValues = new ContentValues();
    contentValues.put(TaskContract.TaskEntry.COLUMN_DESCRIPTION, editStr);
    contentValues.put(TaskContract.TaskEntry.COLUMN_PRIORITY, mPriority);

    Uri returnUri = getContentResolver().insert(TaskContract.TaskEntry.CONTENT_URI, contentValues);
~~~
10. query만들기
~~~
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        final SQLiteDatabase db = mTaskDbHelper.getReadableDatabase();//읽기용으로 가져온다
        int id = sUriMatcher.match(uri);
        Cursor returnCursor;
        switch (id){
            case TASKS :
                returnCursor = db.query(TaskContract.TaskEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);//query함수 파라미터를 넣는다.
                break;
            default:
                throw new UnsupportedOperationException();
        }
        returnCursor.setNotificationUri(getContext().getContentResolver(), uri); //notification 해준다.
        return returnCursor;
    }
~~~
11. query에서 원하는 id만 가져오기 (위에 이어서)
~~~
    case : TASK_WITH_ID
        String id = uri.getPathSegments.get(1)//path를 가져오는데 0이면 tasks 1이면 #을 가져올 것이다.
        String mSelection = "_id=?";
        Strin[] mSelectionArgs = new String[]{id};
        returnCursor = db.query(TaskContract.TaskEntry.TABLE_NAME, projection, mSelection, mSelectionArgs, null, null, sortOrder);//파라미터가 바뀌었다.
        break; //
    }
~~~
