/*****************************************************************************
 * Copyright (C) 2008 EnterpriseDB Corporation.
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 as published by
 * the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, see http://www.gnu.org/licenses or write to the
 * Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301 USA.
 *
 * You can contact EnterpriseDB, Inc. via its website at
 * http://www.enterprisedb.com
 *
 ****************************************************************************/
package com.edb.gridsql.parser.handler;

/**
 * This interface holds ID's for all the Funtions that we support. Please change
 * the MAXID in this file so that we dont assign the same ID to two function.
 */
public interface IFunctionID {
    /**
     * Please update this variable as you bump the numbers up.
     */
    public static int MAXID = 137;

    /**
     * Date Time Function.
     */
    public static int YEAR_ID = 11;

    public static int MONTH_ID = 15;

    public static int MINUTE_ID = 16;

    public static int SECOND_ID = 18;

    public static int USER_ID = 19;

    public static int SUBSTRING_ID = 20;

    public static int TIMESTAMP_ID = 21;

    public static int TRUNC_ID = 23;

    public static int BINARY_ID = 24;

    public static int ADDDATE_ID = 27;

    public static int ADDTIME_ID = 28;

    public static int DATE_ID = 29;

    public static int DATEDIFF_ID = 30;

    public static int DAY_ID = 31;

    public static int DAYNAME_ID = 32;

    public static int DAYOFMONTH_ID = 33;

    public static int DAYOFWEEK_ID = 34;

    public static int DAYOFYEAR_ID = 35;

    public static int MONTHNAME_ID = 36;

    public static int SUBDATE_ID = 37;

    public static int SUBTIME_ID = 38;

    public static int TIME_ID = 39;

    public static int WEEKOFYEAR_ID = 40;

    public static int HOUR_ID = 42;

    // Arthematic Functions
    public static int ABS_ID = 0;

    public static int CEIL_ID = 43;

    public static int EXP_ID = 44;

    public static int FLOOR_ID = 45;

    public static int LN_ID = 46;

    public static int LOG_ID = 47;

    public static int PI_ID = 48;

    public static int POWER_ID = 49;

    public static int SIGN_ID = 50;

    public static int ASIN_ID = 51;

    public static int ATAN_ID = 52;

    public static int COS_ID = 53;

    public static int COT_ID = 54;

    public static int DEGREES_ID = 55;

    public static int RADIANS_ID = 56;

    public static int SIN_ID = 57;

    public static int TAN_ID = 58;

    public static int ROUND_ID = 59;

    public static int ACOS_ID = 87;

    public static int LOG10_ID = 88;

    public static int MOD_ID = 89;

    public static int SQRT_ID = 90;

    public static int COSH_ID = 91;

    public static int FLOAT_ID = 93;

    public static int GREATEST_ID = 94;

    public static int LEAST_ID = 95;

    public static int ATAN2_ID = 96;

    public static int ATN2_ID = 97;

    // String Functions
    public static int ASCII_ID = 60;

    public static int INDEX_ID = 61;

    public static int LEFT_ID = 62;

    public static int LENGTH_ID = 63;

    public static int LOWER_ID = 64;

    public static int LPAD_ID = 65;

    public static int LTRIM_ID = 66;

    public static int REPLACE_ID = 67;

    public static int RIGHT_ID = 68;

    public static int RPAD_ID = 69;

    public static int RTRIM_ID = 70;

    public static int SUBSTR_ID = 71;

    public static int TRIM_ID = 72;

    public static int UPPER_ID = 73;

    public static int INSTR_ID = 74;

    public static int SOUNDEX_ID = 98;

    public static int INITCAP_ID = 99;

    public static int LFILL_ID = 100;

    public static int MAPCHAR_ID = 101;

    public static int NUM_ID = 102;

    public static int CONCAT_ID = 104;

    public static int CUSTOM_ID = 105;

    public static int CAST_ID = 106;

    // For Postgres
    public static int TIMEOFDAY_ID = 107;

    public static int ISFINITE_ID = 108;

    public static int EXTRACT_ID = 109;

    public static int DATETRUNC_ID = 110;

    public static int DATEPART_ID = 111;

    public static int AGE_ID = 112;

    public static int BITLENGTH_ID = 118;

    public static int CHARLENGTH_ID = 119;

    public static int CONVERT_ID = 120;

    public static int OCTETLENGTH_ID = 121;

    public static int OVERLAY_ID = 122;

    public static int POSITION_ID = 123;

    public static int TO_HEX_ID = 124;

    public static int QUOTE_LITERAL_ID = 125;

    public static int QUOTE_IDENT_ID = 126;

    public static int PG_CLIENT_ENCODING_ID = 127;

    public static int MD5_ID = 128;

    public static int CHR_ID = 129;

    public static int TRANSLATE_ID = 130;

    public static int TO_ASCII_ID = 131;

    public static int STRPOS_ID = 132;

    public static int SPLIT_PART_ID = 133;

    public static int REPEAT_ID = 134;

    public static int ENCODE_ID = 135;

    public static int DECODE_ID = 136;

    public static int BTRIM_ID = 137;

    public static int WIDTH_BUCKET_ID = 138;

    public static int SETSEED_ID = 139;

    public static int RANDOM_ID = 140;

    public static int CBRT_ID = 141;
    public static int GETBIT_ID = 142 ;
    public static int CLOCK_TIMESTAMP_ID = 143 ;
    public static int STATEMENT_TIMESTAMP_ID = 144 ;
    public static int TRANSACTION_TIMESTAMP_ID = 145 ;
    public static int GETBYTE_ID = 146 ;
    public static int TODATE_ID = 147 ;
    public static int NULLIF_ID = 148 ;
    public static int SETBIT_ID = 149 ;
    public static int SETBYTE_ID = 150 ;
    public static int TOCHAR_ID = 151 ;
    public static int TONUMBER_ID = 152 ;
    public static int TOTIMESTAMP_ID = 153 ;
    public static int ADDMONTHS_ID = 154 ;
    public static int JUSTIFYDAYS_ID = 155 ;
    public static int JUSTIFYHOURS_ID = 156 ;
    public static int JUSTIFYINTERVAL_ID = 157 ;
    public static int LASTDAY_ID = 158 ;
    public static int MONTHSBETWEEN_ID = 159 ;
    public static int NEXTDAY_ID = 160 ;
    public static int CURRENTDATABASE_ID = 161 ;
    public static int CURRENTSCHEMA_ID = 162 ;
    public static int SYSDATE_ID = 163 ;
    public static int REGEXPREPLACE_ID = 185;
    public static int NVL_ID = 186;
    public static int NVL2_ID = 187;
    public static int COALESCE_ID = 188;
    public static int CURRENTDATE_ID = 189;
    public static int CURRENTTIME_ID = 190;
    public static int CURRENTTIMESTAMP_ID = 191;
    public static int CURRENTUSER_ID = 192;

    // Aggreagate Functions
    public static int AVG_ID = 75;

    public static int COUNT_ID = 76;

    public static int COUNT_STAR_ID = 77;

    public static int STDEV_ID = 78;

    public static int VARIANCE_ID = 79;

    public static int MAX_ID = 80;

    public static int MIN_ID = 81;

    public static int SUM_ID = 82;

    public static int BITAND_ID = 164;

    public static int BITOR_ID = 165;

    public static int BOOLAND_ID = 166;

    public static int BOOLOR_ID = 167;

    public static int EVERY_ID = 168;

    // Statistical Aggreagate Functions
    public static int CORR_ID = 169;

    public static int COVARPOP_ID = 170;

    public static int COVARSAMP_ID = 171;

    public static int REGRAVX_ID = 172;

    public static int REGRAVY_ID = 173;

    public static int REGRCOUNT_ID = 174;

    public static int REGRINTERCEPT_ID = 175;

    public static int REGRR2_ID = 176;

    public static int REGRSLOPE_ID = 177;

    public static int REGRSXX_ID = 178;

    public static int REGRSXY_ID = 179;

    public static int REGRSYY_ID = 180;

    public static int STDEVPOP_ID = 181;

    public static int STDEVSAMP_ID = 182;

    public static int VARIANCEPOP_ID = 183;

    public static int VARIANCESAMP_ID = 184;
    
    // inet and cidr functions
    public static int ABBREV_ID = 193;
    
    public static int BROADCAST_ID = 194;
    
    public static int FAMILY_ID = 195;
    
    public static int HOST_ID = 196;
    
    public static int HOSTMASK_ID = 197;
    
    public static int MASKLEN_ID = 198;
    
    public static int NETMASK_ID = 199;
    
    public static int NETWORK_ID = 200;
    
    public static int SET_MASKLEN_ID = 201;
    
    public static int TEXT_ID = 202;
    
    // Misc Functions

    public static int VERSION_ID = 84;

    public static int VALUE_ID = 85;

    public static int CASE_ID = 86;

}
