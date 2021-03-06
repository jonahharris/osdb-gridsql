//
// Generated by JTB 1.2.2
//

package com.edb.gridsql.parser.core.syntaxtree;

/**
 * Grammar production:
 * f0 -> Func_Trunc(prn)
 *       | Func_Date(prn)
 *       | Func_Time(prn)
 *       | Func_TimeStamp(prn)
 *       | Func_CurrentDate(prn)
 *       | Func_PgCurrentDate(prn)
 *       | Func_CurrentTime(prn)
 *       | Func_PgCurrentTime(prn)
 *       | Func_PgCurrentTimeStamp(prn)
 *       | Func_Year(prn)
 *       | Func_Month(prn)
 *       | Func_Hour(prn)
 *       | Func_Minute(prn)
 *       | Func_Second(prn)
 *       | Func_AddDate(prn)
 *       | Func_AddTime(prn)
 *       | Func_DateDiff(prn)
 *       | Func_Day(prn)
 *       | Func_DayName(prn)
 *       | Func_DayOfMonth(prn)
 *       | Func_DayOfWeek(prn)
 *       | Func_DayOfYear(prn)
 *       | Func_MonthName(prn)
 *       | Func_SubDate(prn)
 *       | Func_SubTime(prn)
 *       | Func_WeekOfYear(prn)
 *       | Func_Now(prn)
 *       | Func_Abs(prn)
 *       | Func_Ceil(prn)
 *       | Func_Ceiling(prn)
 *       | Func_Exp(prn)
 *       | Func_Floor(prn)
 *       | Func_LN(prn)
 *       | Func_Log(prn)
 *       | Func_PI(prn)
 *       | Func_Power(prn)
 *       | Func_Round(prn)
 *       | Func_Sign(prn)
 *       | Func_TAsin(prn)
 *       | Func_TAtan(prn)
 *       | Func_TCos(prn)
 *       | Func_TCot(prn)
 *       | Func_TDegree(prn)
 *       | Func_Radians(prn)
 *       | Func_Sin(prn)
 *       | Func_Tan(prn)
 *       | Func_Avg(prn)
 *       | Func_Count(prn)
 *       | Func_Max(prn)
 *       | Func_Min(prn)
 *       | Func_Stdev(prn)
 *       | Func_Sum(prn)
 *       | Func_Variance(prn)
 *       | Func_Ascii(prn)
 *       | Func_Index(prn)
 *       | Func_Left(prn)
 *       | Func_Length(prn)
 *       | Func_Lower(prn)
 *       | Func_Lpad(prn)
 *       | Func_Rpad(prn)
 *       | Func_Ltrim(prn)
 *       | Func_Replace(prn)
 *       | Func_Right(prn)
 *       | Func_Rtrim(prn)
 *       | Func_SubStr(prn)
 *       | Func_Trim(prn)
 *       | Func_Upper(prn)
 *       | Func_Database(prn)
 *       | Func_Value(prn)
 *       | Func_Version(prn)
 *       | Func_Case(prn)
 *       | Func_TACos(prn)
 *       | Func_Log10(prn)
 *       | Func_Mod(prn)
 *       | Func_Sqrt(prn)
 *       | Func_Least(prn)
 *       | Func_TCosh(prn)
 *       | Func_Float(prn)
 *       | Func_Greatest(prn)
 *       | Func_TATan2(prn)
 *       | Func_TATn2(prn)
 *       | Func_SoundEx(prn)
 *       | Func_InitCap(prn)
 *       | Func_LFill(prn)
 *       | Func_MapChar(prn)
 *       | Func_NUM(prn)
 *       | Func_Concat(prn)
 *       | Func_User(prn)
 *       | Func_Cast(prn)
 *       | Func_TimeOfDay(prn)
 *       | Func_Custom(prn)
 *       | Func_IsFinite(prn)
 *       | Func_Extract(prn)
 *       | Func_DateTrunc(prn)
 *       | Func_DatePart(prn)
 *       | Func_Age(prn)
 *       | Func_LocalTime(prn)
 *       | Func_LocalTimeStamp(prn)
 *       | Func_BitLength(prn)
 *       | Func_CharLength(prn)
 *       | Func_Convert(prn)
 *       | Func_OctetLength(prn)
 *       | Func_Overlay(prn)
 *       | Func_Position(prn)
 *       | Func_Substring(prn)
 *       | Func_ToHex(prn)
 *       | Func_QuoteLiteral(prn)
 *       | Func_QuoteIdent(prn)
 *       | Func_Md5(prn)
 *       | Func_Chr(prn)
 *       | Func_PgClientEncoding(prn)
 *       | Func_Translate(prn)
 *       | Func_ToAscii(prn)
 *       | Func_StrPos(prn)
 *       | Func_SplitPart(prn)
 *       | Func_Repeat(prn)
 *       | Func_Encode(prn)
 *       | Func_Decode(prn)
 *       | Func_Btrim(prn)
 *       | Func_Width_bucket(prn)
 *       | Func_Setseed(prn)
 *       | Func_Random(prn)
 *       | Func_Cbrt(prn)
 *       | Func_GetBit(prn)
 *       | Func_GetByte(prn)
 *       | Func_ToDate(prn)
 *       | Func_ClockTimeStamp(prn)
 *       | Func_StatementTimeStamp(prn)
 *       | Func_TransactionTimeStamp(prn)
 *       | Func_NullIf(prn)
 *       | Func_SetBit(prn)
 *       | Func_SetByte(prn)
 *       | Func_ToChar(prn)
 *       | Func_ToNumber(prn)
 *       | Func_ToTimestamp(prn)
 *       | Func_AddMonths(prn)
 *       | Func_JustifyDays(prn)
 *       | Func_JustifyHours(prn)
 *       | Func_JustifyInterval(prn)
 *       | Func_LastDay(prn)
 *       | Func_MonthsBetween(prn)
 *       | Func_NextDay(prn)
 *       | Func_CurrentDatabase(prn)
 *       | Func_CurrentSchema(prn)
 *       | Func_SysDate(prn)
 *       | Func_BitAnd(prn)
 *       | Func_BitOr(prn)
 *       | Func_BoolAnd(prn)
 *       | Func_BoolOr(prn)
 *       | Func_CorrCov(prn)
 *       | Func_Regr(prn)
 *       | Func_RegexReplace(prn)
 *       | Func_Nvl(prn)
 *       | Func_Coalesce(prn)
 *       | Func_Abbrev(prn)
 *       | Func_Broadcast(prn)
 *       | Func_Family(prn)
 *       | Func_Host(prn)
 *       | Func_Hostmask(prn)
 *       | Func_Masklen(prn)
 *       | Func_Netmask(prn)
 *       | Func_Network(prn)
 *       | Func_Set_Masklen(prn)
 *       | Func_Text(prn)
 */
public class FunctionCall implements Node {
   public NodeChoice f0;

   public FunctionCall(NodeChoice n0) {
      f0 = n0;
   }

   public void accept(com.edb.gridsql.parser.core.visitor.Visitor v) {
      v.visit(this);
   }
   public Object accept(com.edb.gridsql.parser.core.visitor.ObjectVisitor v, Object argu) {
      return v.visit(this,argu);
   }
}

