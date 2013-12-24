<?php

  // The Simple PHP Blog is released under the GNU Public License.
  //
  // You are free to use and modify the Simple PHP Blog. All changes
  // must be uploaded to SourceForge.net under Simple PHP Blog or
  // emailed to apalmo <at> bigevilbrain <dot> com

  // write_dateFormat ( $array )
  // read_dateFormat ()
  // format_date ( $time_stamp )
  // date_convert( $val, $leading_zero_day, $leading_zero_month, $full_century, $time_stamp )

  // -------------------------------
  // Date Config / Display Functions
  // -------------------------------

  function write_dateFormat ( $array ) {
    // Save date/time format information to file.
    //
    $str = implode('|', $array);

    if (!file_exists(CONFIG_DIR)) {
      $oldumask = umask(0);
      $ok = mkdir(CONFIG_DIR, BLOG_MASK );
      umask($oldumask);
    }

    $filename = CONFIG_DIR.'date_format.txt';
    $result = sb_write_file( $filename, $str );

    if ( $result ) {
      return ( true );
    } else {
      // Error:
      // Probably couldn't create file...
      return ( $filename );
    }
  }

  function read_dateFormat () {

    $contents = sb_read_file( CONFIG_DIR.'date_format.txt' );
    if ( $contents ) {
      $array = explode('|', $contents);

      $dateArray[ 'lDate_slotOne' ] = $array[ 0 ];
      $dateArray[ 'lDate_slotOneSeparator' ] = $array[ 1 ];
      $dateArray[ 'lDate_slotTwo' ] = $array[ 2 ];
      $dateArray[ 'lDate_slotTwoSeparator' ] = $array[ 3 ];
      $dateArray[ 'lDate_slotThree' ] = $array[ 4 ];
      $dateArray[ 'lDate_slotThreeSeparator' ] = $array[ 5 ];
      $dateArray[ 'lDate_slotFour' ] = $array[ 6 ];
      $dateArray[ 'lDate_slotFourSeparator' ] = $array[ 7 ];
      $dateArray[ 'lDate_leadZeroDay' ] = $array[ 8 ];
      $dateArray[ 'sDate_order' ] = $array[ 9 ];
      $dateArray[ 'sDate_separator' ] = $array[ 10 ];
      $dateArray[ 'sDate_leadZeroDay' ] = $array[ 11 ];
      $dateArray[ 'sDate_leadZeroMonth' ] = $array[ 12 ];
      $dateArray[ 'sDate_fullYear' ] = $array[ 13 ];
      $dateArray[ 'time_clockFormat' ] = $array[ 14 ];
      $dateArray[ 'time_leadZeroHour' ] = $array[ 15 ];
      $dateArray[ 'time_AM' ] = $array[ 16 ];
      $dateArray[ 'time_PM' ] = $array[ 17 ];
      $dateArray[ 'time_separator' ] = $array[ 18 ];
      $dateArray[ 'eFormat_slotOne' ] = $array[ 19 ];
      $dateArray[ 'eFormat_separator' ] = $array[ 20 ];
      $dateArray[ 'eFormat_slotTwo' ] = $array[ 21 ];
      $dateArray[ 'server_offset' ] = $array[ 22 ];
      $dateArray[ 'mFormat' ] = $array[ 23 ];
    } else {
      $dateArray = array();
      $dateArray[ 'lDate_slotOne' ] = 'weekday';
      $dateArray[ 'lDate_slotOneSeparator' ] = ', ';
      $dateArray[ 'lDate_slotTwo' ] = 'month';
      $dateArray[ 'lDate_slotTwoSeparator' ] = ' ';
      $dateArray[ 'lDate_slotThree' ] = 'day';
      $dateArray[ 'lDate_slotThreeSeparator' ] = ', ';
      $dateArray[ 'lDate_slotFour' ] = 'year';
      $dateArray[ 'lDate_slotFourSeparator' ] = '';
      $dateArray[ 'lDate_leadZeroDay' ] = '';
      $dateArray[ 'sDate_order' ] = 'Month/Day/Year';
      $dateArray[ 'sDate_separator' ] = '/';
      $dateArray[ 'sDate_leadZeroDay' ] = '';
      $dateArray[ 'sDate_leadZeroMonth' ] = '';
      $dateArray[ 'sDate_fullYear' ] = '';
      $dateArray[ 'time_clockFormat' ] = '12';
      $dateArray[ 'time_leadZeroHour' ] = 'on';
      $dateArray[ 'time_AM' ] = ' AM';
      $dateArray[ 'time_PM' ] = ' PM';
      $dateArray[ 'time_separator' ] = ':';
      $dateArray[ 'eFormat_slotOne' ] = 'long';
      $dateArray[ 'eFormat_separator' ] = ', ';
      $dateArray[ 'eFormat_slotTwo' ] = 'time';
      $dateArray[ 'server_offset' ] = '0';
      $dateArray[ 'mFormat' ] = 'short';
    }

    return ( $dateArray );
  }

  function format_date ( $time_stamp ) {
    if ( strpos( $time_stamp, ',' ) !== false ) {
      // This is a hack for compatibility with the time
      // format from versions < 0.3.3. In 0.3.3 we switched
      // to the unix timestamp for storing times.
      //
      // Before that it was in this format:
      //   date( 'F j, Y, g:i a', $time_stamp );
      //   'May 10, 2004, 3:57 pm'
      $time_stamp = str_replace( ',', '', $time_stamp );
      $time_stamp = strtotime( $time_stamp );
    }

    // Read config information from file.

    $dateArray = read_dateFormat();

    $time_stamp = $time_stamp + ( intval( $dateArray[ 'server_offset' ] ) * 60 * 60);

    // Long Date
    $date_long = '';
    $date_long  .= date_convert( $dateArray[ 'lDate_slotOne' ], $dateArray[ 'lDate_leadZeroDay' ], 'off', 'on', $time_stamp );
    $date_long  .= $dateArray[ 'lDate_slotOneSeparator' ];
    $date_long  .= date_convert( $dateArray[ 'lDate_slotTwo' ], $dateArray[ 'lDate_leadZeroDay' ], 'off', 'on', $time_stamp );
    $date_long  .= $dateArray[ 'lDate_slotTwoSeparator' ];
    $date_long  .= date_convert( $dateArray[ 'lDate_slotThree' ], $dateArray[ 'lDate_leadZeroDay' ], 'off', 'on', $time_stamp );
    $date_long  .= $dateArray[ 'lDate_slotThreeSeparator' ];
    $date_long  .= date_convert( $dateArray[ 'lDate_slotFour' ], $dateArray[ 'lDate_leadZeroDay' ], 'off', 'on', $time_stamp );
    $date_long  .= $dateArray[ 'lDate_slotFourSeparator' ];

    // Short Date
    $date_short = '';
    $separator = $dateArray[ 'sDate_separator' ];
    $leading_zero_day = $dateArray[ 'sDate_leadZeroDay' ];
    $leading_zero_month = $dateArray[ 'sDate_leadZeroMonth' ];
    $full_century = $dateArray[ 'sDate_fullYear' ];
    switch ( $dateArray[ 'sDate_order' ] ) {
      case 'Month/Day/Year':
        $date_short  .= date_convert( 'month_decimal', $leading_zero_day, $leading_zero_month, $full_century, $time_stamp );
        $date_short  .= $separator;
        $date_short  .= date_convert( 'day', $leading_zero_day, $leading_zero_month, $full_century, $time_stamp );
        $date_short  .= $separator;
        $date_short  .= date_convert( 'year', $leading_zero_day, $leading_zero_month, $full_century, $time_stamp );
        break;
      case 'Day/Month/Year':
        $date_short  .= date_convert( 'day', $leading_zero_day, $leading_zero_month, $full_century, $time_stamp );
        $date_short  .= $separator;
        $date_short  .= date_convert( 'month_decimal', $leading_zero_day, $leading_zero_month, $full_century, $time_stamp );
        $date_short  .= $separator;
        $date_short  .= date_convert( 'year', $leading_zero_day, $leading_zero_month, $full_century, $time_stamp );
        break;
      case 'Year/Month/Day':
        $date_short  .= date_convert( 'year', $leading_zero_day, $leading_zero_month, $full_century, $time_stamp );
        $date_short  .= $separator;
        $date_short  .= date_convert( 'month_decimal', $leading_zero_day, $leading_zero_month, $full_century, $time_stamp );
        $date_short  .= $separator;
        $date_short  .= date_convert( 'day', $leading_zero_day, $leading_zero_month, $full_century, $time_stamp );
        break;
      case 'Month/Year/Day':
        $date_short  .= date_convert( 'month_decimal', $leading_zero_day, $leading_zero_month, $full_century, $time_stamp );
        $date_short  .= $separator;
        $date_short  .= date_convert( 'year', $leading_zero_day, $leading_zero_month, $full_century, $time_stamp );
        $date_short  .= $separator;
        $date_short  .= date_convert( 'day', $leading_zero_day, $leading_zero_month, $full_century, $time_stamp );
        break;
      case 'Day/Year/Month':
        $date_short  .= date_convert( 'day', $leading_zero_day, $leading_zero_month, $full_century, $time_stamp );
        $date_short  .= $separator;
        $date_short  .= date_convert( 'year', $leading_zero_day, $leading_zero_month, $full_century, $time_stamp );
        $date_short  .= $separator;
        $date_short  .= date_convert( 'month_decimal', $leading_zero_day, $leading_zero_month, $full_century, $time_stamp );
        break;
      case 'Year/Day/Month':
        $date_short  .= date_convert( 'year', $leading_zero_day, $leading_zero_month, $full_century, $time_stamp );
        $date_short  .= $separator;
        $date_short  .= date_convert( 'day', $leading_zero_day, $leading_zero_month, $full_century, $time_stamp );
        $date_short  .= $separator;
        $date_short  .= date_convert( 'month_decimal', $leading_zero_day, $leading_zero_month, $full_century, $time_stamp );
        break;
      case 'Day/MMM/Year':
        $date_short  .= date_convert( 'day', $leading_zero_day, $leading_zero_month, $full_century, $time_stamp );
        $date_short  .= $separator;
        $date_short  .= date_convert( 'month_short', $leading_zero_day, $leading_zero_month, $full_century, $time_stamp );
        $date_short  .= $separator;
        $date_short  .= date_convert( 'year', $leading_zero_day, $leading_zero_month, $full_century, $time_stamp );
        break;
    }

    // Time View
    $time_str = '';
    $time_clockFormat = $dateArray[ 'time_clockFormat' ];
    $leading_zero_hour = $dateArray[ 'time_leadZeroHour' ];
    $before_noon = $dateArray[ 'time_AM' ];
    $after_noon = $dateArray[ 'time_PM' ];
    $separator = $dateArray[ 'time_separator' ];

    if ( $time_clockFormat == '24' ) {
      if ( $leading_zero_hour == 'on' ) {
        $time_str  .= date( 'H', $time_stamp ) . $separator . date( 'i', $time_stamp );
      } else {
        $time_str  .= date( 'G', $time_stamp ) . $separator . date( 'i', $time_stamp );
      }
    } else {
      if ( $leading_zero_hour == 'on' ) {
        $time_str  .= date( 'h', $time_stamp ) . $separator . date( 'i', $time_stamp );
        if ( date( 'a', $time_stamp ) == 'am' ) {
          $time_str  .= $before_noon;
        } else {
          $time_str  .= $after_noon;
        }
      } else {
        $time_str  .= date( 'g', $time_stamp ) . $separator . date( 'i', $time_stamp );
        if ( date( 'a', $time_stamp ) == 'am' ) {
          $time_str  .= $before_noon;
        } else {
          $time_str  .= $after_noon;
        }
      }
    }

    // Put it all together...
    $str = '';
    switch ( $dateArray[ 'eFormat_slotOne' ] ) {
      case 'long':
        $str  .= $date_long;
        break;
      case 'short':
        $str  .= $date_short;
        break;
      case 'time':
        $str  .= $time_str;
        break;
      case 'none':
        break;
    }

    $str  .= $dateArray[ 'eFormat_separator' ];

    switch ( $dateArray[ 'eFormat_slotTwo' ] ) {
      case 'long':
        $str  .= $date_long;
        break;
      case 'short':
        $str  .= $date_short;
        break;
      case 'time':
        $str  .= $time_str;
        break;
      case 'none':
        break;
    }

    return ( clean_post_text( $str ) );
  }

  function format_date_class ( $time_stamp,$whatyouwant ) {
    if ( strpos( $time_stamp, ',' ) !== false ) {
      // This is a hack for compatibility with the time
      // format from versions < 0.3.3. In 0.3.3 we switched
      // to the unix timestamp for storing times.
      //
      // Before that it was in this format:
      //   date( 'F j, Y, g:i a', $time_stamp );
      //   'May 10, 2004, 3:57 pm'
      $time_stamp = str_replace( ',', '', $time_stamp );
      $time_stamp = strtotime( $time_stamp );
    }

    // Read config information from file.

    $dateArray = read_dateFormat();

    $time_stamp = $time_stamp + ( intval( $dateArray[ 'server_offset' ] ) * 60 * 60);

    // Long Date
    $date_long = '';
    $date_long  .= date_convert( $dateArray[ 'lDate_slotOne' ], $dateArray[ 'lDate_leadZeroDay' ], 'off', 'on', $time_stamp );
    $date_long  .= $dateArray[ 'lDate_slotOneSeparator' ];
    $date_long  .= date_convert( $dateArray[ 'lDate_slotTwo' ], $dateArray[ 'lDate_leadZeroDay' ], 'off', 'on', $time_stamp );
    $date_long  .= $dateArray[ 'lDate_slotTwoSeparator' ];
    $date_long  .= date_convert( $dateArray[ 'lDate_slotThree' ], $dateArray[ 'lDate_leadZeroDay' ], 'off', 'on', $time_stamp );
    $date_long  .= $dateArray[ 'lDate_slotThreeSeparator' ];
    $date_long  .= date_convert( $dateArray[ 'lDate_slotFour' ], $dateArray[ 'lDate_leadZeroDay' ], 'off', 'on', $time_stamp );
    $date_long  .= $dateArray[ 'lDate_slotFourSeparator' ];

    // Short Date
    $date_short = '';
    $separator = $dateArray[ 'sDate_separator' ];
    $leading_zero_day = $dateArray[ 'sDate_leadZeroDay' ];
    $leading_zero_month = $dateArray[ 'sDate_leadZeroMonth' ];
    $full_century = $dateArray[ 'sDate_fullYear' ];
    $numeric_day = date_convert( 'day', $leading_zero_day, $leading_zero_month, $full_century, $time_stamp );
    $numeric_month = date_convert( 'month_decimal', $leading_zero_day, $leading_zero_month, $full_century, $time_stamp );
    $numeric_year = date_convert( 'year', $leading_zero_day, $leading_zero_month, $full_century, $time_stamp );
    $alpha_month = date_convert( 'month_short', $leading_zero_day, $leading_zero_month, $full_century, $time_stamp );
    $numeric_day_suffix = date_convert( 'day_suffix', $leading_zero_day, $leading_zero_month, $full_century, $time_stamp );

    // Time View
    $time_str = '';
    $time_clockFormat = $dateArray[ 'time_clockFormat' ];
    $leading_zero_hour = $dateArray[ 'time_leadZeroHour' ];
    $before_noon = $dateArray[ 'time_AM' ];
    $after_noon = $dateArray[ 'time_PM' ];
    $separator = $dateArray[ 'time_separator' ];

    if ( $time_clockFormat == '24' ) {
      if ( $leading_zero_hour == 'on' ) {
        $time_str  .= date( 'H', $time_stamp ) . $separator . date( 'i', $time_stamp );
      } else {
        $time_str  .= date( 'G', $time_stamp ) . $separator . date( 'i', $time_stamp );
      }
    } else {
      if ( $leading_zero_hour == 'on' ) {
        $time_str  .= date( 'h', $time_stamp ) . $separator . date( 'i', $time_stamp );
        if ( date( 'a', $time_stamp ) == 'am' ) {
          $time_str  .= $before_noon;
        } else {
          $time_str  .= $after_noon;
        }
      } else {
        $time_str  .= date( 'g', $time_stamp ) . $separator . date( 'i', $time_stamp );
        if ( date( 'a', $time_stamp ) == 'am' ) {
          $time_str  .= $before_noon;
        } else {
          $time_str  .= $after_noon;
        }
      }
    }

    // OK, we've got it...
    $str = '';
    switch( $whatyouwant ) {
      case 'NUMDAY':
        $str  .= $numeric_day;
        break;
      case 'NUMMONTH':
        $str  .= $numeric_month;
        break;
      case 'NUMYEAR':
        $str  .= "'" . $numeric_year;
        break;
      case 'ALPHAMONTH':
        $str  .= $alpha_month;
        break;
      case 'TIMENORMAL':
        $str  .= $time_str;
        break;
      case 'SUFFIXDAY':
        $str  .= $numeric_day_suffix;
        break;
    }

    return ( clean_post_text( $str ) );
  }

  function date_convert( $val, $leading_zero_day, $leading_zero_month, $full_century, $time_stamp ) {
    // Return string dates in the correct format.
    //

    $str = '';
    if ( $val == 'weekday' ) {
      // Monday
      $str = strftime( '%A', $time_stamp );
    } else if ( $val == 'month' ) {
      // January
      $str = strftime( '%B', $time_stamp );
    } else if ( $val == 'month_short' ) {
      // Jan
      $str = strftime( '%b', $time_stamp );
    } else if ( $val == 'month_decimal' ) {
      if ( $leading_zero_month == 'on' ) {
        $str = date( 'm', $time_stamp );
      } else {
        $str = date( 'n', $time_stamp );
      }
    } else if ( $val == 'day' ) {
      if ( $leading_zero_day == 'on' ) {
        $str = date( 'd', $time_stamp );
      } else {
        $str = date( 'j', $time_stamp );
      }
    } else if ( $val == 'day_suffix' ) {
      if ( $leading_zero_day == 'on' ) {
        $str = date( 'dS', $time_stamp );
      } else {
        $str = date( 'jS', $time_stamp );
      }
    } else if ( $val == 'year' ) {
      if ( $full_century == 'on' ) {
        $str = date( 'Y', $time_stamp );
      } else {
        $str = date( 'y', $time_stamp );
      }
    } else if ( $val == 'none' ) {
      $str = '';
    }

    return $str;
  }
?>
