		// <!--
		
		function longdate_view() {
			// Update the Long Date format preview field.
			
			var str = '';
			str = str + date_pulldown( document.forms[ 'setup' ][ 'lDate_slotOne' ].value, document.forms[ 'setup' ][ 'lDate_leadZeroDay' ].value, 'off', 'on' );
			str = str + document.forms[ 'setup' ][ 'lDate_slotOneSeparator' ].value;
			str = str + date_pulldown( document.forms[ 'setup' ][ 'lDate_slotTwo' ].value, document.forms[ 'setup' ][ 'lDate_leadZeroDay' ].value, 'off', 'on' );
			str = str + document.forms[ 'setup' ][ 'lDate_slotTwoSeparator' ].value;
			str = str + date_pulldown( document.forms[ 'setup' ][ 'lDate_slotThree' ].value, document.forms[ 'setup' ][ 'lDate_leadZeroDay' ].value, 'off', 'on' );
			str = str + document.forms[ 'setup' ][ 'lDate_slotThreeSeparator' ].value;
			str = str + date_pulldown( document.forms[ 'setup' ][ 'lDate_slotFour' ].value, document.forms[ 'setup' ][ 'lDate_leadZeroDay' ].value, 'off', 'on' );
			str = str + document.forms[ 'setup' ][ 'lDate_slotFourSeparator' ].value;
			
			document.forms[ 'setup' ][ 'longdate_preview' ].value = str;
		}
		
		function date_pulldown( val, leading_zero_day, leading_zero_month, full_century ) {
			// Return string dates in the correct format for the preview fields.
			
			var str = '';
			if ( val == 'weekday' ) {
				// Monday
				str = <?php echo( '\'' . strftime( '%A' ) . '\'' ); ?>;
			} else if ( val == 'month' ) {
				// January
				str = <?php echo( '\'' . strftime( '%B' ) . '\'' ); ?>;
			} else if ( val == 'month_short' ) {
				// Jan
				str = <?php echo( '\'' . strftime( '%b' ) . '\'' ); ?>;
			} else if ( val == 'month_decimal' ) {
				if ( leading_zero_month == 'on' ) {
					str = '01';
				} else {
					str = '1';
				}
			} else if ( val == 'day' ) {
				if ( leading_zero_day == 'on' ) {
					str = '09';
				} else {
					str = '9';
				}
			} else if ( val == 'year' ) {
				if ( full_century == 'on' ) {
					str = '2004';
				} else {
					str = '04';
				}
			} else if ( val == 'none' ) {
				str = '';
			}
			return str;
		}
		
		function shortdate_view() {
			// Update the Short Date format preview field.
			
			var str = '';
			var separator = document.forms[ 'setup' ][ 'sDate_separator' ].value;
			var leading_zero_day = document.forms[ 'setup' ][ 'sDate_leadZeroDay' ].value;
			var leading_zero_month = document.forms[ 'setup' ][ 'sDate_leadZeroMonth' ].value;
			var full_century = document.forms[ 'setup' ][ 'sDate_fullYear' ].value;
			
			switch ( document.forms[ 'setup' ][ 'sDate_order' ].value ) {
				case 'Month/Day/Year':
					str = str + date_pulldown( 'month_decimal', leading_zero_day, leading_zero_month, full_century );
					str = str + separator;
					str = str + date_pulldown( 'day', leading_zero_day, leading_zero_month, full_century );
					str = str + separator;
					str = str + date_pulldown( 'year', leading_zero_day, leading_zero_month, full_century );
					break;
				case 'Day/Month/Year':
					str = str + date_pulldown( 'day', leading_zero_day, leading_zero_month, full_century );
					str = str + separator;
					str = str + date_pulldown( 'month_decimal', leading_zero_day, leading_zero_month, full_century );
					str = str + separator;
					str = str + date_pulldown( 'year', leading_zero_day, leading_zero_month, full_century );
					break;
				case 'Year/Month/Day':
					str = str + date_pulldown( 'year', leading_zero_day, leading_zero_month, full_century );
					str = str + separator;
					str = str + date_pulldown( 'month_decimal', leading_zero_day, leading_zero_month, full_century );
					str = str + separator;
					str = str + date_pulldown( 'day', leading_zero_day, leading_zero_month, full_century );
					break;
				case 'Month/Year/Day':
					str = str + date_pulldown( 'month_decimal', leading_zero_day, leading_zero_month, full_century );
					str = str + separator;
					str = str + date_pulldown( 'year', leading_zero_day, leading_zero_month, full_century );
					str = str + separator;
					str = str + date_pulldown( 'day', leading_zero_day, leading_zero_month, full_century );
					break;
				case 'Day/Year/Month':
					str = str + date_pulldown( 'day', leading_zero_day, leading_zero_month, full_century );
					str = str + separator;
					str = str + date_pulldown( 'year', leading_zero_day, leading_zero_month, full_century );
					str = str + separator;
					str = str + date_pulldown( 'month_decimal', leading_zero_day, leading_zero_month, full_century );
					break;
				case 'Year/Day/Month':
					str = str + date_pulldown( 'year', leading_zero_day, leading_zero_month, full_century );
					str = str + separator;
					str = str + date_pulldown( 'day', leading_zero_day, leading_zero_month, full_century );
					str = str + separator;
					str = str + date_pulldown( 'month_decimal', leading_zero_day, leading_zero_month, full_century );
					break;
				case 'Day/MMM/Year':
					str = str + date_pulldown( 'day', leading_zero_day, leading_zero_month, full_century );
					str = str + separator;
					str = str + date_pulldown( 'month_short', leading_zero_day, leading_zero_month, full_century );
					str = str + separator;
					str = str + date_pulldown( 'year', leading_zero_day, leading_zero_month, full_century );
					break;
			}
			
			document.forms[ 'setup' ][ 'shortdate_preview' ].value = str;
			
		}
		
		function time_view() {
			// Update the Time format preview field.
			
			var str = '';
			
			var leading_zero_hour = document.forms[ 'setup' ][ 'time_leadZeroHour' ].value;
			var before_noon = document.forms[ 'setup' ][ 'time_AM' ].value;
			var after_noon = document.forms[ 'setup' ][ 'time_PM' ].value;
			var separator = document.forms[ 'setup' ][ 'time_separator' ].value;
			
			for ( i=0; i<document.forms[ 'setup' ][ 'time_clockFormat' ].length; i++ ) {
				if ( document.forms[ 'setup' ][ 'time_clockFormat' ][i].checked ) {
					var time_clockFormat = document.forms[ 'setup' ][ 'time_clockFormat' ][i].value;
				}
			}
			
			if ( time_clockFormat == "24" ) {
				if ( leading_zero_hour == 'on' ) {
					str = str + "00" + separator + "34";
					str = str + "		";
					str = str + "16" + separator + "56";
				} else {
					str = str + "0" + separator + "34";
					str = str + "		";
					str = str + "16" + separator + "56";
				}
			} else {
				if ( leading_zero_hour == 'on' ) {
					str = str + "12" + separator + "34" + before_noon;
					str = str + "		";
					str = str + "04" + separator + "56" + after_noon;
				} else {
					str = str + "12" + separator + "34" + before_noon;
					str = str + "		";
					str = str + "4" + separator + "56" + after_noon;
				}
			}
			
			document.forms[ 'setup' ][ 'time_preview' ].value = str;
		}
		
		function dateInit() {
			longdate_view();
			shortdate_view();
			time_view();
		}
		
		// TODO this is not defined?!
		//addEvent(window, 'load', dateInit, false);
		// -->
