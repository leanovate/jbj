<?php
require_once('../../scripts/sb_functions.php');

    $content_width = $theme_vars[ 'content_width' ];
    $menu_width = $theme_vars[ 'menu_width' ];
    $page_width = $content_width + $menu_width;

    $header_graphic = $blog_config->getTag('BLOG_HEADER_GRAPHIC');
    if ( $header_graphic == '' ) {
       $header_graphic = 'images/header750x100.jpg';
    } else {
       $header_graphic = '../../' . $header_graphic;
    }

    // Begin Page Layout HTML

    $oppo = 'left';
    if ($theme_vars[ 'menu_align' ] == 'left') {
        $oppo = 'right';
    }


header("Content-Type: text/css");
?>

  body {
    background-color: #<?php echo(get_user_color('bg_color')); ?>;
    color: #<?php echo(get_user_color('txt_color')); ?>;
    font-size: .7em;
    margin: 0px;
    padding: 0px;
    /* Standard. Readable */
    font-family: Arial, Helvetica, Sans-Serif;
  }

h1, h2, h3, h4, h5, h6
{
  font-family: 'Trebuchet MS', 'Gill Sans', Helvetica, sans-serif;
  font-weight: bold;
  color: #666633; /* headline_txt_color */
    text-shadow: #bbb 2px 2px 1px;

  margin: 2px 0px 2px 0px;
}

  p
  {
    margin: 8px 0px 8px 0px;
  }

  hr  
  {
    color: #<?php echo(get_user_color('inner_border_color')); ?>;
    background-color: #<?php echo(get_user_color('inner_border_color')); ?>;
    margin: 8px 0px 8px 0px;
  }

  img {
    border-style: none;
  }

  form {
    font-size: 1em;
  } 

  input, select, option, textarea
  {
    font-size: 1em;  
    text-align: left;
  }

  a:link, a:visited {
    color: #<?php echo(get_user_color('link_reg_color')); ?>;
    font-weight: bold;
    text-decoration: none;
  }

  a:hover {
    text-decoration: underline;
    color: #<?php echo(get_user_color('link_hi_color')); ?>;
  }

  a:active {
    color: #<?php echo(get_user_color('link_down_color')); ?>;
  }

  code, pre {
    font-family: 'Courier New', Courier, Fixed;
  }
  
  pre {
    max-width: <?php global $theme_vars; echo( $theme_vars[ 'max_image_width' ] ); ?>px;
    overflow: auto;
    border: 1px dotted #<?php echo(get_user_color('inner_border_color')); ?>;
    padding: 5px;
  }

blockquote {
  color: #777;
  margin: 15px 30px 0 10px;
  padding-left: 20px;
  border-left: 5px solid #ddd
}


  #page {
    background-color: #<?php echo(get_user_color('main_bg_color')); ?>;
    max-width: <?php echo( $page_width ); ?>px;
    margin-left:auto;
    margin-right:auto;
    border: 1px solid #<?php echo(get_user_color('border_color')); ?>;
  }

  #header {
    background-repeat: no-repeat;
    background-image: url('<?php echo( $header_graphic ); ?>');
    min-height: 25px;
    border-color: #<?php echo(get_user_color('border_color')); ?>;
    color: #<?php echo(get_user_color('header_txt_color')); ?>;
    background-color: #<?php echo(get_user_color('header_bg_color')); ?>;
    font-family: 'Trebuchet MS', 'Gill Sans', Helvetica, sans-serif;
    margin: 0px;
    padding: 105px 5px 5px 5px;
    border-width: 0px 0px 1px 0px;
    border-style: solid;
    font-size: 1.4em;
    font-weight: bold;
  }

  #footer {
    clear: both;
    background-color: #<?php echo(get_user_color('footer_bg_color')); ?>;
    color: #<?php echo(get_user_color('footer_txt_color')); ?>;
    background: #<?php echo(get_user_color('footer_bg_color')); ?>;
    border-top: 1px solid #<?php echo(get_user_color('border_color')); ?>;
    padding: 10px;
    text-align: left;
  }

  h1, h2, h3, h4, h5, h6 {
    color: #<?php echo(get_user_color('headline_txt_color')); ?>;
  }

  #maincontent .blog_subject {
    color: #<?php echo(get_user_color('headline_txt_color')); ?>;
    font-family: 'Trebuchet MS', 'Gill Sans', Helvetica, sans-serif;
    font-size: 1.4em;
    font-weight: bold;
    margin: 0px;
  }

  #maincontent .blog_subject img
  {
    /* padding: top right bottom left */
    padding: 0px 0px 0px 10px;
    float: right;
  }

  #maincontent .blog_date {
    color: #<?php echo(get_user_color('date_txt_color')); ?>;
  }

  #maincontent .blog_categories {
    color: #<?php echo(get_user_color('date_txt_color')); ?>;
  }

  #maincontent .blog_body_solid  {
    color: #<?php echo(get_user_color('txt_color')); ?>;
    background-color: #<?php echo(get_user_color('bg_color')); ?>;
    border-color: #<?php echo(get_user_color('txt_color')); ?>;
    padding: 1px;
    border-width: 0px 0px 1px 0px;
    border-style: solid;
  }

#maincontent .blog_body_clear
{
  padding: 0px;
  border-color: #FFF;
  border-width: 0px;
  border-style: solid;
}

#maincontent .blog_byline
{
  color: #999999; /* date_txt_color */
  font-size: .9em;
  margin-bottom: 10px;
}

  
  #maincontent {
    max-width: <?php echo $theme_vars[ 'content_width' ]-25; ?>px;
    background-color: #<?php echo(get_user_color('main_bg_color')); ?>;
    padding: 10px;
  }

  #sidebar {
    max-width: <?php echo $theme_vars[ 'menu_width' ]-20; ?>px;
    float: <?php echo $theme_vars[ 'menu_align' ] ?>;
    background-color: #<?php echo(get_user_color('menu_bg_color')); ?>;
    border-<?php echo $oppo; ?>: 1px solid #<?php echo(get_user_color('inner_border_color')); ?>;
    border-bottom: 1px solid #<?php echo(get_user_color('inner_border_color')); ?>;
    padding: 10px;
  }

  #sidebar .menu_body {
    border-color: #<?php echo(get_user_color('inner_border_color')); ?>;
    padding: 10px;
    border-width: 1px;
    border-style: dashed;
  }

#sidebar .menu_title
{
  font-family: 'Trebuchet MS', 'Gill Sans', Helvetica, sans-serif;
  font-weight: bold;
}

#sidebar .menu_title a
{
  text-decoration: none;
  color: inherit;
}


  #archive_tree_menu li
  {
    margin: 0px;
    padding: 0px;
    /* border: 1px #F0F dashed; */
  }

#sidebar .divider {
  margin: 8px 0px 8px 0px;
}

#sidebar .calendar a
{
  font-weight: bold;
  text-decoration: none;
}

.divider { margin: 20px 0px 15px 0px; }


	
/* -------------------- Tables -------------------- */

.data_table table {
	width: 100%;
	border-collapse: collapse;
	border-spacing: 0;
	padding-bottom: .4em;
	margin-bottom: .8em;
	background: white;
	}
	.data_table th, .data_table td {
		padding: .2em .4em 0 .4em;
		border: 1px #b5d0dc solid;
		text-align: left;
		vertical-align: top;
		}
	.data_table th {
		background: #f0f6f8;
		font-weight: bold;
		}
	.data_table caption {
		padding: .4em .4em .2em .4em;
		font-size: 1.1em;
		background: #dae7ed;
		border: 1px #b5d0dc solid;
		border-bottom: none;
		text-align: left;
		vertical-align: top;
		}
			
	/* -------------------- Forms -------------------- */

	input { cursor: pointer; }
	
	input[type='text'],
	input[type='password'],
	textarea {
		/* color: #2e2812;
		background: #fefaec;
		*/
		cursor: text;
		}
		
	input[type='button'],
	input[type='submit'],
	select {
		vertical-align: middle;
		}
		
	input[type='checkbox'] {
		vertical-align: bottom;
		}
	
	legend {
		padding: 0 6px;
		}
		
	fieldset {
		padding: 10px;
		margin: .8em 0;
		border: 1px solid #aac0e9;
		}
	
	select { padding: .1em .2em 0 .2em; cursor: pointer; }
	option { padding: 0 .4em; }
	
	input[type='text']:focus,
	input[type='password']:focus,
	textarea:focus {
		background: white;
	}
	
	.linerule {
		border-bottom: 1px dotted #aac0e9;
		margin-bottom: 1.4em;
	}

/* -------------------- Admin Forms -------------------- */

.defaultform {
	width: 480px;
	}
	
	/* Supporting Styles */
	
	.defaultform label {
		font-size: 1.0em;
		}
	
	.defaultform .note {
		font-size: 0.9em;
		line-height: 1.3em;
		margin-bottom: 0.5em;
		display: inline-block;
		color: #92865c;
		}
	.defaultform .optional {
		color: #4AAB27;
		font-size: 0.7em;
		text-transform: uppercase;
		font-weight: bold;
		}
		
	.defaultform textarea,
	.defaultform input[type='text'],
	.defaultform input[type='password'] {
		width: 458px; /* w-22 */
		}
	
	
	/* Field width = (form width / n) - 22 */
	/* Column width = (form width / n) - 16 */
	
	/* These are explicit classes for IE6 which can't handle the psuedo-selectors input[type='text'] */
	
	.defaultform .single { width: 458px; }
	.defaultform .double { width: 218px; }
	.defaultform .triple { width: 138px; }
	.defaultform .quadruple { width: 98px; }
	
	/* One Column */
	.defaultform .column_single {
		float: left;
		display: block;
		width: 464px;
		margin-right: 15px;
		margin-bottom: 10px;
		}
		.defaultform .column_single textarea,
		.defaultform .column_single input[type='text'],
		.defaultform .column_single input[type='password'] {
			width: 458px;
			}
			
	/* Two Column */
	.defaultform .column_double {
		float: left;
		display: block;
		width: 224px;
		margin-right: 15px;
		margin-bottom: 10px;
		}
		.defaultform .column_double textarea,
		.defaultform .column_double input[type='text'],
		.defaultform .column_double input[type='password'] {
			width: 218px;
			}
	
	/* Three Column */
	.defaultform .column_triple {
		float: left;
		display: block;
		width: 144px;
		margin-right: 15px;
		margin-bottom: 10px;
		}
		.defaultform .column_triple textarea,
		.defaultform .column_triple input[type='text'],
		.defaultform .column_triple input[type='password'] {
			width: 138px;
			}
	
	/* Three Column */
	.defaultform .column_quadruple {
		float: left;
		display: block;
		width: 104px;
		margin-right: 15px;
		margin-bottom: 10px;
		}
		.defaultform .column_quadruple textarea,
		.defaultform .column_quadruple input[type='text'],
		.defaultform .column_quadruple input[type='password'] {
			width: 98px;
			}

                div #toggleSetupLanguage, #toggleSetupGeneral, #toggleSetupEntries, #toggleSetupSidebar, #toggleSetupTrackbacks, #toggleSetupComments, #toggleSetupCompression
                {
                        border-color: #<?php echo(get_user_color('inner_border_color')); ?>;
                }

