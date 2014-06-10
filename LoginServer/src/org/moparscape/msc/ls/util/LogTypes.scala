package org.moparscape.msc.ls.util

class TradeLog(val from: Long, val to: Long, val item: Int, val amount: Long, val x: Int, val y: Int, val t: Int, val date: Long = System.currentTimeMillis / 1000)
class ReportLog(val user: Long, val reported: Long, val reason: Byte, val x: Int, val y: Int, val status: String, val date: Long = System.currentTimeMillis / 1000)
class KillLog(val user: Long, val killed: Long, val t: Byte, val date: Long = System.currentTimeMillis / 1000)
class BanLog(val user: Long, val mod: Long, val date: Long = System.currentTimeMillis / 1000)
class LoginLog(val user: Long, val ip: String, val date: Long = System.currentTimeMillis / 1000)
