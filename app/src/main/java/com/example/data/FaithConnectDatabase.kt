package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        Member::class,
        Department::class,
        AttendanceRecord::class,
        ChurchEvent::class,
        MemberNote::class
    ],
    version = 1,
    exportSchema = false
)
abstract class FaithConnectDatabase : RoomDatabase() {
    abstract fun dao(): FaithConnectDao

    companion object {
        @Volatile
        private var INSTANCE: FaithConnectDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): FaithConnectDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FaithConnectDatabase::class.java,
                    "faithconnect_database"
                )
                    .addCallback(DatabaseCallback(scope))
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialData(database.dao())
                    }
                }
            }
        }

        suspend fun populateInitialData(dao: FaithConnectDao) {
            // Departments
            val departments = listOf(
                Department(
                    id = "choir",
                    name = "Choir & Music Ministry",
                    leaderName = "Min. Emmanuel Vance",
                    description = "Vocalists, instrumentalists, and worship leaders ushering the congregation into God's presence.",
                    meetingTime = "Saturdays 5:00 PM & Sundays 7:30 AM",
                    iconCategory = "music",
                    roomOrVenue = "Main Choir Loft"
                ),
                Department(
                    id = "ushering",
                    name = "Ushering & Hospitality",
                    leaderName = "Bro. David Miller",
                    description = "Welcoming members and guests with warmth, distributing bulletins, and maintaining orderly sanctuary seating.",
                    meetingTime = "1st & 3rd Sundays 8:00 AM",
                    iconCategory = "hospitality",
                    roomOrVenue = "Sanctuary Foyer"
                ),
                Department(
                    id = "youth",
                    name = "Youth & Young Adults",
                    leaderName = "Sis. Chloe Adebayo",
                    description = "Dynamic ministry equipping teens and young professionals to live boldly for Christ in university and marketplace.",
                    meetingTime = "Fridays 6:30 PM",
                    iconCategory = "group",
                    roomOrVenue = "Youth Pavilion"
                ),
                Department(
                    id = "children",
                    name = "Children's Church (Kingdom Kids)",
                    leaderName = "Deaconess Ruth Sterling",
                    description = "Engaging biblical foundation, crafts, songs, and memory verses for nursery through elementary ages.",
                    meetingTime = "Sundays 9:30 AM",
                    iconCategory = "child",
                    roomOrVenue = "Children's Wing"
                ),
                Department(
                    id = "men",
                    name = "Men of Faith Fellowship",
                    leaderName = "Elder Marcus Wright",
                    description = "Brothers in Christ fostering spiritual leadership, accountability, brotherhood, and community service.",
                    meetingTime = "2nd Saturdays 8:00 AM Breakfast",
                    iconCategory = "men",
                    roomOrVenue = "Fellowship Hall"
                ),
                Department(
                    id = "women",
                    name = "Women of Grace",
                    leaderName = "Pastor Sarah Jenkins",
                    description = "Nurturing women of prayer, wisdom, intercession, family mentorship, and charitable community outreach.",
                    meetingTime = "3rd Saturdays 10:00 AM",
                    iconCategory = "women",
                    roomOrVenue = "Chapel Room"
                ),
                Department(
                    id = "media",
                    name = "Media & Audio-Visual",
                    leaderName = "Bro. Kevin Zhao",
                    description = "Sound engineering, livestream broadcast, camera operation, lyrics projection, and social media production.",
                    meetingTime = "Sundays 8:00 AM & Thursdays 6:00 PM",
                    iconCategory = "tech",
                    roomOrVenue = "AV Production Booth"
                ),
                Department(
                    id = "outreach",
                    name = "Evangelism & Community Care",
                    leaderName = "Elder Thomas Okafor",
                    description = "Hospital visitation, prison outreach, local food pantry distributions, and street evangelism drives.",
                    meetingTime = "Last Saturday of each month 9:00 AM",
                    iconCategory = "outreach",
                    roomOrVenue = "Community Resource Center"
                )
            )
            dao.insertDepartments(departments)

            // Initial Members
            val members = listOf(
                Member(
                    id = 1,
                    fullName = "Marcus Sterling",
                    phone = "+1 (555) 234-5678",
                    email = "marcus.sterling@gmail.com",
                    address = "742 Evergreen Terrace, Springfield",
                    dateJoined = "2021-03-14",
                    departments = "Men of Faith Fellowship, Ushering & Hospitality",
                    householdRole = "Head of Household",
                    householdName = "The Sterling Family",
                    status = "Active",
                    gender = "Male",
                    ageGroup = "Adult"
                ),
                Member(
                    id = 2,
                    fullName = "Ruth Sterling",
                    phone = "+1 (555) 234-5679",
                    email = "ruth.sterling@gmail.com",
                    address = "742 Evergreen Terrace, Springfield",
                    dateJoined = "2021-03-14",
                    departments = "Children's Church (Kingdom Kids), Women of Grace",
                    householdRole = "Spouse",
                    householdName = "The Sterling Family",
                    status = "Active",
                    gender = "Female",
                    ageGroup = "Adult"
                ),
                Member(
                    id = 3,
                    fullName = "Joshua Sterling",
                    phone = "+1 (555) 234-5680",
                    email = "josh.sterling@school.org",
                    address = "742 Evergreen Terrace, Springfield",
                    dateJoined = "2021-03-14",
                    departments = "Youth & Young Adults",
                    householdRole = "Child",
                    householdName = "The Sterling Family",
                    status = "Active",
                    gender = "Male",
                    ageGroup = "Youth"
                ),
                Member(
                    id = 4,
                    fullName = "Chloe Adebayo",
                    phone = "+1 (555) 345-6789",
                    email = "chloe.adebayo@outlook.com",
                    address = "1204 Oakridge Ave, Springfield",
                    dateJoined = "2022-01-09",
                    departments = "Youth & Young Adults, Choir & Music Ministry",
                    householdRole = "Individual",
                    householdName = "Adebayo Household",
                    status = "Active",
                    gender = "Female",
                    ageGroup = "Youth"
                ),
                Member(
                    id = 5,
                    fullName = "Emmanuel Vance",
                    phone = "+1 (555) 456-7890",
                    email = "emmanuel.vance@soundmusic.org",
                    address = "88 Harmony Way, Springfield",
                    dateJoined = "2019-08-22",
                    departments = "Choir & Music Ministry",
                    householdRole = "Head of Household",
                    householdName = "The Vance Family",
                    status = "Active",
                    gender = "Male",
                    ageGroup = "Adult"
                ),
                Member(
                    id = 6,
                    fullName = "David Miller",
                    phone = "+1 (555) 567-8901",
                    email = "david.miller@harvestcorp.com",
                    address = "312 Cedar Blvd, Springfield",
                    dateJoined = "2020-11-05",
                    departments = "Ushering & Hospitality, Men of Faith Fellowship",
                    householdRole = "Head of Household",
                    householdName = "The Miller Family",
                    status = "Active",
                    gender = "Male",
                    ageGroup = "Adult"
                ),
                Member(
                    id = 7,
                    fullName = "Hannah Washington",
                    phone = "+1 (555) 678-9012",
                    email = "hannah.w@gracehealth.net",
                    address = "550 Magnolia Court, Springfield",
                    dateJoined = "2023-05-18",
                    departments = "Women of Grace, Choir & Music Ministry",
                    householdRole = "Individual",
                    householdName = "Washington Household",
                    status = "Active",
                    gender = "Female",
                    ageGroup = "Adult"
                ),
                Member(
                    id = 8,
                    fullName = "Kevin Zhao",
                    phone = "+1 (555) 789-0123",
                    email = "kzhao.av@gmail.com",
                    address = "18 Riverbend Parkway, Springfield",
                    dateJoined = "2022-09-12",
                    departments = "Media & Audio-Visual",
                    householdRole = "Head of Household",
                    householdName = "The Zhao Family",
                    status = "Active",
                    gender = "Male",
                    ageGroup = "Adult"
                ),
                Member(
                    id = 9,
                    fullName = "Thomas Okafor",
                    phone = "+1 (555) 890-1234",
                    email = "tokafor@evangel.org",
                    address = "404 Sunset Drive, Springfield",
                    dateJoined = "2018-04-10",
                    departments = "Evangelism & Community Care, Men of Faith Fellowship",
                    householdRole = "Head of Household",
                    householdName = "The Okafor Family",
                    status = "Active",
                    gender = "Male",
                    ageGroup = "Senior"
                ),
                Member(
                    id = 10,
                    fullName = "Abigail Okafor",
                    phone = "+1 (555) 890-1235",
                    email = "abigail.okafor@carenet.org",
                    address = "404 Sunset Drive, Springfield",
                    dateJoined = "2018-04-10",
                    departments = "Women of Grace, Evangelism & Community Care",
                    householdRole = "Spouse",
                    householdName = "The Okafor Family",
                    status = "Active",
                    gender = "Female",
                    ageGroup = "Senior"
                ),
                Member(
                    id = 11,
                    fullName = "Samuel O'Connor",
                    phone = "+1 (555) 901-2345",
                    email = "sam.oconnor@techlab.io",
                    address = "77 Willow Creek Lane, Springfield",
                    dateJoined = "2024-02-11",
                    departments = "Media & Audio-Visual, Youth & Young Adults",
                    householdRole = "Individual",
                    householdName = "O'Connor Household",
                    status = "New Member",
                    gender = "Male",
                    ageGroup = "Youth"
                ),
                Member(
                    id = 12,
                    fullName = "Grace Montgomery",
                    phone = "+1 (555) 012-3456",
                    email = "gmontgomery@elderlycare.com",
                    address = "901 Birchwood Circle, Springfield",
                    dateJoined = "2015-06-20",
                    departments = "Women of Grace",
                    householdRole = "Head of Household",
                    householdName = "Montgomery Household",
                    status = "Honorary",
                    gender = "Female",
                    ageGroup = "Senior"
                ),
                Member(
                    id = 13,
                    fullName = "Lucas Bennett",
                    phone = "+1 (555) 123-4560",
                    email = "lucas.bennett@campus.edu",
                    address = "214 College Hill Rd, Springfield",
                    dateJoined = "2024-08-15",
                    departments = "Youth & Young Adults, Choir & Music Ministry",
                    householdRole = "Individual",
                    householdName = "Bennett Household",
                    status = "New Member",
                    gender = "Male",
                    ageGroup = "Youth"
                ),
                Member(
                    id = 14,
                    fullName = "Deborah Vance",
                    phone = "+1 (555) 456-7891",
                    email = "deborah.vance@home.net",
                    address = "88 Harmony Way, Springfield",
                    dateJoined = "2019-08-22",
                    departments = "Children's Church (Kingdom Kids)",
                    householdRole = "Spouse",
                    householdName = "The Vance Family",
                    status = "Active",
                    gender = "Female",
                    ageGroup = "Adult"
                ),
                Member(
                    id = 15,
                    fullName = "Elijah Sterling",
                    phone = "+1 (555) 234-5681",
                    email = "elijah.sterling@kids.org",
                    address = "742 Evergreen Terrace, Springfield",
                    dateJoined = "2021-03-14",
                    departments = "Children's Church (Kingdom Kids)",
                    householdRole = "Child",
                    householdName = "The Sterling Family",
                    status = "Active",
                    gender = "Male",
                    ageGroup = "Child"
                ),
                Member(
                    id = 16,
                    fullName = "Miriam Patel",
                    phone = "+1 (555) 987-6543",
                    email = "miriam.patel@cityhospital.org",
                    address = "323 Blossom Way, Springfield",
                    dateJoined = "2023-10-01",
                    departments = "Ushering & Hospitality",
                    householdRole = "Head of Household",
                    householdName = "The Patel Family",
                    status = "Active",
                    gender = "Female",
                    ageGroup = "Adult"
                )
            )
            dao.insertMembers(members)

            // Historical Attendance Records (Weekly trend data)
            val attendances = listOf(
                AttendanceRecord(
                    id = 1,
                    serviceName = "Sunday Morning Celebration",
                    date = "2026-09-06",
                    mode = "Checklist",
                    adultCount = 210,
                    childCount = 48,
                    guestCount = 18,
                    totalCount = 276,
                    notes = "Communion Sunday. Great altar call response."
                ),
                AttendanceRecord(
                    id = 2,
                    serviceName = "Sunday Morning Celebration",
                    date = "2026-09-13",
                    mode = "Checklist",
                    adultCount = 225,
                    childCount = 52,
                    guestCount = 22,
                    totalCount = 299,
                    notes = "Back to School blessing for youth & teachers."
                ),
                AttendanceRecord(
                    id = 3,
                    serviceName = "Sunday Morning Celebration",
                    date = "2026-09-20",
                    mode = "Headcount",
                    adultCount = 238,
                    childCount = 55,
                    guestCount = 25,
                    totalCount = 318,
                    notes = "Youth Sunday takeover service."
                ),
                AttendanceRecord(
                    id = 4,
                    serviceName = "Sunday Morning Celebration",
                    date = "2026-09-27",
                    mode = "Checklist",
                    adultCount = 242,
                    childCount = 58,
                    guestCount = 28,
                    totalCount = 328,
                    notes = "Evangelism impact Sunday."
                ),
                AttendanceRecord(
                    id = 5,
                    serviceName = "Sunday Morning Celebration",
                    date = "2026-10-04",
                    mode = "Checklist",
                    adultCount = 255,
                    childCount = 62,
                    guestCount = 31,
                    totalCount = 348,
                    notes = "Harvest & Thanksgiving Service. Record high attendance!"
                ),
                AttendanceRecord(
                    id = 6,
                    serviceName = "Sunday Morning Celebration",
                    date = "2026-10-11",
                    mode = "Checklist",
                    adultCount = 248,
                    childCount = 60,
                    guestCount = 27,
                    totalCount = 335,
                    notes = "Family life series kick-off."
                ),
                AttendanceRecord(
                    id = 7,
                    serviceName = "Sunday Morning Celebration",
                    date = "2026-10-18",
                    mode = "Checklist",
                    adultCount = 262,
                    childCount = 66,
                    guestCount = 34,
                    totalCount = 362,
                    presentMemberIds = "1,2,3,4,5,6,7,8,9,10,14,15,16",
                    notes = "Powerful word preached by Pastor David. 4 new guest families."
                ),
                AttendanceRecord(
                    id = 8,
                    serviceName = "Midweek Bible Study",
                    date = "2026-10-21",
                    mode = "Checklist",
                    adultCount = 142,
                    childCount = 24,
                    guestCount = 12,
                    totalCount = 178,
                    presentMemberIds = "1,2,5,6,7,8,9,10,12",
                    notes = "Book of Romans Chapter 8 study & corporate prayer."
                )
            )
            for (att in attendances) {
                dao.insertAttendanceRecord(att)
            }

            // Church Events
            val events = listOf(
                ChurchEvent(
                    id = 1,
                    title = "Sunday Divine Celebration Service",
                    date = "2026-10-25",
                    time = "9:00 AM - 11:30 AM",
                    department = "General Church",
                    location = "Main Sanctuary",
                    description = "Join us for inspiring praise, heartfelt worship, and life-changing sermon on 'Walking in Divine Favor'.",
                    isRecurringWeekly = true
                ),
                ChurchEvent(
                    id = 2,
                    title = "Kingdom Youth Camp Fire & Fellowship",
                    date = "2026-10-26",
                    time = "6:30 PM - 8:30 PM",
                    department = "Youth & Young Adults",
                    location = "Fellowship Pavilion Grounds",
                    description = "Outdoor campfire, worship unplugged, s'mores, and interactive Q&A for high school and university students."
                ),
                ChurchEvent(
                    id = 3,
                    title = "Midweek Prayer & Communion Night",
                    date = "2026-10-28",
                    time = "6:30 PM - 8:00 PM",
                    department = "General Church",
                    location = "Chapel & Online Stream",
                    description = "Midweek spiritual recharge, verse-by-verse teaching through Psalms, and partaking in the Holy Communion."
                ),
                ChurchEvent(
                    id = 4,
                    title = "Choir & Orchestra Mass Rehearsal",
                    date = "2026-10-31",
                    time = "4:30 PM - 6:30 PM",
                    department = "Choir & Music Ministry",
                    location = "Choir Suite",
                    description = "Preparation for upcoming Harvest Jubilee and Christmas cantata songs."
                ),
                ChurchEvent(
                    id = 5,
                    title = "Men of Faith Monthly Breakfast",
                    date = "2026-11-07",
                    time = "8:00 AM - 10:00 AM",
                    department = "Men of Faith Fellowship",
                    location = "Fellowship Hall",
                    description = "Hearty breakfast, fellowship, and guest speaker addressing 'Integrity in the Marketplace and Home'."
                ),
                ChurchEvent(
                    id = 6,
                    title = "Women of Grace Prayer Tea",
                    date = "2026-11-14",
                    time = "10:00 AM - 1:00 PM",
                    department = "Women of Grace",
                    location = "Garden Pavilion",
                    description = "An uplifting morning of prayer, tea, testimonies, and sisterly bonding in Christ."
                ),
                ChurchEvent(
                    id = 7,
                    title = "Thanksgiving Community Food Drive",
                    date = "2026-11-21",
                    time = "9:00 AM - 2:00 PM",
                    department = "Evangelism & Community Care",
                    location = "Church Parking Lot & Community Center",
                    description = "Distributing over 500 Thanksgiving holiday grocery hampers to local underserved families."
                )
            )
            dao.insertEvents(events)

            // Pastoral Notes & Communication Log
            val notes = listOf(
                MemberNote(
                    id = 1,
                    memberId = 1, // Marcus Sterling
                    authorName = "Pastor David",
                    date = "2026-10-15",
                    category = "Pastoral Visit",
                    content = "Visited the Sterling family home. Marcus shared great enthusiasm about mentoring junior ushers. Prayed for business expansion and family peace."
                ),
                MemberNote(
                    id = 2,
                    memberId = 2, // Ruth Sterling
                    authorName = "Pastor Sarah",
                    date = "2026-10-08",
                    category = "Milestone",
                    content = "Discussed curriculum for Kingdom Kids holiday camp. Ruth is doing phenomenal work organizing teachers and craft volunteers."
                ),
                MemberNote(
                    id = 3,
                    memberId = 4, // Chloe Adebayo
                    authorName = "Pastor David",
                    date = "2026-09-29",
                    category = "Counseling",
                    content = "Career counseling session. Chloe accepted an offer in medical research. We thanked God and prayed for her upcoming transition."
                ),
                MemberNote(
                    id = 4,
                    memberId = 11, // Samuel O'Connor
                    authorName = "Pastor David",
                    date = "2026-10-12",
                    category = "Follow-up",
                    content = "Followed up after new member orientation. Samuel completed the membership class and is thrilled to operate cameras on Sunday mornings."
                ),
                MemberNote(
                    id = 5,
                    memberId = 12, // Grace Montgomery
                    authorName = "Pastor Sarah",
                    date = "2026-10-17",
                    category = "Prayer Request",
                    content = "Telephone check-in and prayer. Sister Grace requested prayers for her granddaughter who is expecting twins next month. Spirits are high and full of joy."
                )
            )
            dao.insertNotes(notes)
        }
    }
}
