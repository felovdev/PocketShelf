package dev.pocketshelf;

import android.app.*;
import android.os.Bundle;
import android.content.*;
import android.database.Cursor;
import android.database.sqlite.*;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.*;
import android.widget.*;
import android.text.*;

public class MainActivity extends Activity {
 private ShelfDb helper; private LinearLayout items; private EditText search; private TextView count;
 private final int ink=Color.rgb(25,35,48), muted=Color.rgb(103,116,132), accent=Color.rgb(38,105,91);
 private int dp(float n){return (int)(n*getResources().getDisplayMetrics().density+.5f);}
 private GradientDrawable shape(int color){GradientDrawable d=new GradientDrawable();d.setColor(color);d.setCornerRadius(dp(16));return d;}
 private TextView label(String s,int size,int color,boolean bold){TextView t=new TextView(this);t.setText(s);t.setTextSize(size);t.setTextColor(color);if(bold)t.setTypeface(Typeface.DEFAULT,Typeface.BOLD);return t;}
 @Override public void onCreate(Bundle b){super.onCreate(b);helper=new ShelfDb(this);getWindow().setStatusBarColor(Color.rgb(246,248,246));getWindow().setNavigationBarColor(Color.rgb(246,248,246));getWindow().getDecorView().setSystemUiVisibility(8192|16);ui();
 String shared=getIntent().getStringExtra(Intent.EXTRA_TEXT);if(Intent.ACTION_SEND.equals(getIntent().getAction())&&shared!=null&&!shared.trim().isEmpty())editor("",shared,"");render("");}
 private void ui(){
  LinearLayout root=new LinearLayout(this);root.setOrientation(1);root.setPadding(dp(22),dp(24),dp(22),dp(16));root.setBackgroundColor(Color.rgb(246,248,246));setContentView(root);
  root.addView(label("PocketShelf",30,ink,true));TextView sub=label("Your stuff, always handy.",14,muted,false);LinearLayout.LayoutParams sm=new LinearLayout.LayoutParams(-1,-2);sm.topMargin=dp(4);root.addView(sub,sm);
  search=new EditText(this);search.setSingleLine(true);search.setTextSize(15);search.setHint("Search links, notes, tags…");search.setPadding(dp(16),0,dp(16),0);search.setBackground(shape(Color.WHITE));LinearLayout.LayoutParams se=new LinearLayout.LayoutParams(-1,dp(52));se.topMargin=dp(22);root.addView(search,se);
  LinearLayout bar=new LinearLayout(this);bar.setGravity(Gravity.CENTER_VERTICAL);bar.setPadding(0,dp(20),0,dp(10));bar.addView(label("YOUR SHELF",12,muted,true));count=label("0 SAVED",11,accent,true);count.setGravity(Gravity.RIGHT|Gravity.CENTER_VERTICAL);bar.addView(count,new LinearLayout.LayoutParams(0,-2,1));root.addView(bar);
  ScrollView scroll=new ScrollView(this);scroll.setFillViewport(true);items=new LinearLayout(this);items.setOrientation(1);scroll.addView(items);root.addView(scroll,new LinearLayout.LayoutParams(-1,0,1));
  Button add=new Button(this);add.setText("+   Save something");add.setTextSize(15);add.setTextColor(Color.WHITE);add.setAllCaps(false);add.setBackground(shape(accent));LinearLayout.LayoutParams ap=new LinearLayout.LayoutParams(-1,dp(54));ap.topMargin=dp(12);root.addView(add,ap);add.setOnClickListener(v->editor("","",""));
  search.addTextChangedListener(new TextWatcher(){public void beforeTextChanged(CharSequence s,int st,int c,int a){}public void onTextChanged(CharSequence s,int st,int before,int c){render(s.toString());}public void afterTextChanged(Editable e){}});
 }
 private void editor(String oldTitle,String oldBody,String oldTags){
  LinearLayout form=new LinearLayout(this);form.setOrientation(1);form.setPadding(dp(4),dp(8),dp(4),0);
  EditText title=new EditText(this);title.setSingleLine(true);title.setHint("Title (e.g. Useful website)");title.setText(oldTitle);
  EditText body=new EditText(this);body.setHint("Paste a link, note, command or anything useful");body.setMinLines(3);body.setGravity(Gravity.TOP);body.setText(oldBody);
  EditText tags=new EditText(this);tags.setSingleLine(true);tags.setHint("Tags, separated by commas");tags.setText(oldTags);form.addView(title);form.addView(body);form.addView(tags);
  new AlertDialog.Builder(this).setTitle("Save to PocketShelf").setView(form).setNegativeButton("Cancel",null).setPositiveButton("Save",(d,w)->{
   String text=body.getText().toString().trim();if(text.isEmpty()){Toast.makeText(this,"Add some text first",Toast.LENGTH_SHORT).show();return;}
   String name=title.getText().toString().trim();if(name.isEmpty())name=text.length()>36?text.substring(0,36)+"…":text;
   ContentValues cv=new ContentValues();cv.put("title",name);cv.put("body",text);cv.put("tags",tags.getText().toString().trim());cv.put("created",System.currentTimeMillis());helper.getWritableDatabase().insert("items",null,cv);search.setText("");render("");Toast.makeText(this,"Saved to your shelf",Toast.LENGTH_SHORT).show();
  }).show();
 }
 private void render(String q){
  if(items==null)return;items.removeAllViews();Cursor c=helper.getReadableDatabase().rawQuery("SELECT id,title,body,tags FROM items WHERE title LIKE ? OR body LIKE ? OR tags LIKE ? ORDER BY created DESC",new String[]{"%"+q+"%","%"+q+"%","%"+q+"%"});int n=c.getCount();count.setText(n+" SAVED");
  if(n==0){LinearLayout empty=new LinearLayout(this);empty.setOrientation(1);empty.setGravity(Gravity.CENTER);empty.setPadding(dp(16),dp(54),dp(16),dp(54));empty.addView(label(q.isEmpty()?"Your shelf is empty":"Nothing found",19,ink,true));TextView hint=label(q.isEmpty()?"Save a link, a snippet, or a note.\nYou’ll find it here when you need it.":"Try another search term.",14,muted,false);hint.setGravity(Gravity.CENTER);LinearLayout.LayoutParams hp=new LinearLayout.LayoutParams(-1,-2);hp.topMargin=dp(10);empty.addView(hint,hp);items.addView(empty);}
  while(c.moveToNext()){final long id=c.getLong(0);String title=c.getString(1),body=c.getString(2),tags=c.getString(3);LinearLayout card=new LinearLayout(this);card.setOrientation(1);card.setPadding(dp(16),dp(14),dp(16),dp(14));card.setBackground(shape(Color.WHITE));LinearLayout.LayoutParams cp=new LinearLayout.LayoutParams(-1,-2);cp.bottomMargin=dp(10);items.addView(card,cp);card.addView(label(title,16,ink,true));TextView preview=label(body,14,muted,false);preview.setMaxLines(3);LinearLayout.LayoutParams pp=new LinearLayout.LayoutParams(-1,-2);pp.topMargin=dp(7);card.addView(preview,pp);
   if(tags!=null&&!tags.trim().isEmpty()){TextView tv=label("# "+tags,12,accent,true);LinearLayout.LayoutParams tp=new LinearLayout.LayoutParams(-1,-2);tp.topMargin=dp(10);card.addView(tv,tp);}
   card.setOnClickListener(v->{Intent share=new Intent(Intent.ACTION_SEND);share.setType("text/plain");share.putExtra(Intent.EXTRA_TEXT,title+"\n"+body);startActivity(Intent.createChooser(share,"Share saved item"));});
   card.setOnLongClickListener(v->{new AlertDialog.Builder(this).setTitle("Delete saved item?").setMessage(title).setNegativeButton("Cancel",null).setPositiveButton("Delete",(dlg,which)->{helper.getWritableDatabase().delete("items","id=?",new String[]{String.valueOf(id)});render(search.getText().toString());}).show();return true;});
  }c.close();
 }
 static class ShelfDb extends SQLiteOpenHelper {ShelfDb(Context c){super(c,"pocketshelf.db",null,1);}public void onCreate(SQLiteDatabase d){d.execSQL("CREATE TABLE items (id INTEGER PRIMARY KEY AUTOINCREMENT,title TEXT NOT NULL,body TEXT NOT NULL,tags TEXT,created INTEGER NOT NULL)");}public void onUpgrade(SQLiteDatabase d,int oldV,int newV){}}
}