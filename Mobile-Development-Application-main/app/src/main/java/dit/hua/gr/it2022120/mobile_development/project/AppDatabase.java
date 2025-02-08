package dit.hua.gr.it2022120.mobile_development.project;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {Task.class, Status.class}, version = 2)
public abstract class AppDatabase extends RoomDatabase {
    public abstract TaskDao taskDao();

    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "tasks_db")
                            .addMigrations(MIGRATION_1_2)
                            .fallbackToDestructiveMigration()
                            .build();

                }
            }
        }
        return INSTANCE;
    }


    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE status_new (" +
                    "task_id INTEGER PRIMARY KEY NOT NULL, " +
                    "status_name TEXT NOT NULL, " +
                    "FOREIGN KEY(task_id) REFERENCES tasks(id) ON DELETE CASCADE)");
            database.execSQL("INSERT INTO status_new (task_id, status_name) SELECT task_id, status_name FROM status");
            database.execSQL("DROP TABLE status");
            database.execSQL("ALTER TABLE status_new RENAME TO status");
        }
    };

    public void closeDatabase() {
        if (INSTANCE != null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE.isOpen()) {
                    INSTANCE.close();
                }
                INSTANCE = null;
            }
        }
    }

}
