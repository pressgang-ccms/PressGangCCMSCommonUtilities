/*
  This file is part of PresGang CCMS.

  PresGang CCMS is free software: you can redistribute it and/or modify
  it under the terms of the GNU Lesser General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.

  PresGang CCMS is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU Lesser General Public License for more details.

  You should have received a copy of the GNU Lesser General Public License
  along with PresGang CCMS.  If not, see <http://www.gnu.org/licenses/>.
*/

package org.jboss.pressgang.ccms.utils.common;

import static com.google.common.base.Strings.isNullOrEmpty;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.google.code.regexp.Matcher;
import com.google.code.regexp.Pattern;
import org.jboss.pressgang.ccms.utils.structures.DocBookVersion;
import org.jboss.pressgang.ccms.utils.structures.Pair;
import org.jboss.pressgang.ccms.utils.structures.StringToNodeCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

/**
 * A collection of static variables and functions that can be used when working
 * with DocBook
 */
public class DocBookUtilities {
    public static final String TRAILING_WHITESPACE_RE = "^(?<content>.*?)\\s+$";
    public static final String TRAILING_WHITESPACE_SIMPLE_RE = ".*?\\s+$";
    public static final String PRECEEDING_WHITESPACE_SIMPLE_RE = "^\\s+.*";
    public static final Pattern TRAILING_WHITESPACE_RE_PATTERN = Pattern.compile(TRAILING_WHITESPACE_RE,
            java.util.regex.Pattern.MULTILINE | java.util.regex.Pattern.DOTALL);
    public static final Pattern TRAILING_WHITESPACE_SIMPLE_RE_PATTERN = Pattern.compile(TRAILING_WHITESPACE_SIMPLE_RE,
            java.util.regex.Pattern.MULTILINE | java.util.regex.Pattern.DOTALL);
    public static final Pattern PRECEEDING_WHITESPACE_SIMPLE_RE_PATTERN = Pattern.compile(PRECEEDING_WHITESPACE_SIMPLE_RE,
            java.util.regex.Pattern.MULTILINE | java.util.regex.Pattern.DOTALL);
    private static final Logger LOG = LoggerFactory.getLogger(DocBookUtilities.class);

    // See http://stackoverflow.com/a/4307261/1330640
    public static final String UNICODE_WORD = "\\pL\\pM\\p{Nd}\\p{Nl}\\p{Pc}[\\p{InEnclosedAlphanumerics}&&\\p{So}]";
    public static final String UNICODE_TITLE_START_CHAR = "\\pL\\p{Nd}\\p{Nl}";

    private static final Pattern THURSDAY_DATE_RE = Pattern.compile("Thurs?(?!s?day)", java.util.regex.Pattern.CASE_INSENSITIVE);
    private static final Pattern TUESDAY_DATE_RE = Pattern.compile("Tues(?!day)", java.util.regex.Pattern.CASE_INSENSITIVE);

    /**
     * The name of the section tag
     */
    public static final String TOPIC_ROOT_NODE_NAME = "section";
    /**
     * The name of the id attribute
     */
    public static final String TOPIC_ROOT_ID_ATTRIBUTE = "id";
    /**
     * The name of the title tag
     */
    public static final String TOPIC_ROOT_TITLE_NODE_NAME = "title";
    /**
     * The name of the sectioninfo tag
     */
    public static final String TOPIC_ROOT_SECTIONINFO_NODE_NAME = "sectioninfo";

    public static final List<String> TRANSLATABLE_ELEMENTS_OLD = Arrays.asList("ackno", "bridgehead", "caption", "conftitle",
            "contrib", "entry", "firstname", "glossterm", "indexterm", "jobtitle", "keyword", "label", "lastname", "lineannotation",
            "lotentry", "member", "orgdiv", "orgname", "othername", "para", "phrase", "productname", "refclass", "refdescriptor",
            "refentrytitle", "refmiscinfo", "refname", "refpurpose", "releaseinfo", "revremark", "screeninfo", "secondaryie",
            "seealsoie", "seeie", "seg", "segtitle", "simpara", "subtitle", "surname", "term", "termdef", "tertiaryie", "title",
            "titleabbrev", "screen", "programlisting", "literallayout");

    /**
     * The Docbook elements that contain translatable text
     */
    public static final List<String> TRANSLATABLE_ELEMENTS = Arrays.asList("ackno", "bridgehead", "caption", "conftitle", "contrib",
            "entry", "firstname", "glossentry", "indexterm", "jobtitle", "keyword", "label", "lastname", "lineannotation", "lotentry",
            "member", "orgdiv", "orgname", "othername", "para", "phrase", "productname", "refclass", "refdescriptor", "refentrytitle",
            "refmiscinfo", "refname", "refpurpose", "releaseinfo", "revremark", "screeninfo", "secondaryie", "seealsoie", "seeie", "seg",
            "segtitle", "simpara", "subtitle", "surname", "td", "term", "termdef", "tertiaryie", "textobject", "title", "titleabbrev",
            "screen", "programlisting", "literallayout");
    /**
     * The Docbook elements that contain translatable text, and need to be kept inline
     */
    public static final ArrayList<String> INLINE_ELEMENTS = CollectionUtilities.toArrayList(
            new String[]{"footnote", "citerefentry", "indexterm", "orgname", "productname", "phrase", "textobject"});
    /**
     * The Docbook elements that should not have their text reformatted
     */
    public static final ArrayList<String> VERBATIM_ELEMENTS = CollectionUtilities.toArrayList(
            new String[]{"screen", "programlisting", "literallayout"});
    /**
     * The Docbook elements that should be translated only if their parent is not listed in TRANSLATABLE_ELEMENTS
     */
    public static final ArrayList<String> TRANSLATABLE_IF_STANDALONE_ELEMENTS = CollectionUtilities.toArrayList(
            new String[]{"indexterm", "productname", "phrase"});

    /**
     * The standard DocBook XML entities
     */
    public static final Map<String, String> DOCBOOK_ENTITIES = new HashMap<String, String>() {{
        put("euro", "&#x20AC;");
        put("cularr", "&#x021B6;");
        put("curarr", "&#x021B7;");
        put("dArr", "&#x021D3;");
        put("darr2", "&#x021CA;");
        put("dharl", "&#x021C3;");
        put("dharr", "&#x021C2;");
        put("dlarr", "&#x02199;");
        put("drarr", "&#x02198;");
        put("hArr", "&#x021D4;");
        put("harr", "&#x02194;");
        put("harrw", "&#x021AD;");
        put("lAarr", "&#x021DA;");
        put("Larr", "&#x0219E;");
        put("larr2", "&#x021C7;");
        put("larrhk", "&#x021A9;");
        put("larrlp", "&#x021AB;");
        put("larrtl", "&#x021A2;");
        put("lhard", "&#x021BD;");
        put("lharu", "&#x021BC;");
        put("lrarr2", "&#x021C6;");
        put("lrhar2", "&#x021CB;");
        put("lsh", "&#x021B0;");
        put("map", "&#x021A6;");
        put("mumap", "&#x022B8;");
        put("nearr", "&#x02197;");
        put("nhArr", "&#x021CE;");
        put("nharr", "&#x021AE;");
        put("nlArr", "&#x021CD;");
        put("nlarr", "&#x0219A;");
        put("nrArr", "&#x021CF;");
        put("nrarr", "&#x0219B;");
        put("nwarr", "&#x02196;");
        put("olarr", "&#x021BA;");
        put("orarr", "&#x021BB;");
        put("rAarr", "&#x021DB;");
        put("Rarr", "&#x021A0;");
        put("rarr2", "&#x021C9;");
        put("rarrhk", "&#x021AA;");
        put("rarrlp", "&#x021AC;");
        put("rarrtl", "&#x021A3;");
        put("rarrw", "&#x0219D;");
        put("rhard", "&#x021C1;");
        put("rharu", "&#x021C0;");
        put("rlarr2", "&#x021C4;");
        put("rlhar2", "&#x021CC;");
        put("rsh", "&#x021B1;");
        put("uArr", "&#x021D1;");
        put("uarr2", "&#x021C8;");
        put("uharl", "&#x021BF;");
        put("uharr", "&#x021BE;");
        put("vArr", "&#x021D5;");
        put("varr", "&#x02195;");
        put("xhArr", "&#x027FA;");
        put("xharr", "&#x027F7;");
        put("xlArr", "&#x027F8;");
        put("xrArr", "&#x027F9;");
        put("amalg", "&#x02A3F;");
        put("Barwed", "&#x02306;");
        put("barwed", "&#x02305;");
        put("Cap", "&#x022D2;");
        put("coprod", "&#x02210;");
        put("Cup", "&#x022D3;");
        put("cuvee", "&#x022CE;");
        put("cuwed", "&#x022CF;");
        put("diam", "&#x022C4;");
        put("divonx", "&#x022C7;");
        put("intcal", "&#x022BA;");
        put("lthree", "&#x022CB;");
        put("ltimes", "&#x022C9;");
        put("minusb", "&#x0229F;");
        put("oast", "&#x0229B;");
        put("ocir", "&#x0229A;");
        put("odash", "&#x0229D;");
        put("odot", "&#x02299;");
        put("ominus", "&#x02296;");
        put("oplus", "&#x02295;");
        put("osol", "&#x02298;");
        put("otimes", "&#x02297;");
        put("plusb", "&#x0229E;");
        put("plusdo", "&#x02214;");
        put("prod", "&#x0220F;");
        put("rthree", "&#x022CC;");
        put("rtimes", "&#x022CA;");
        put("sdot", "&#x022C5;");
        put("sdotb", "&#x022A1;");
        put("setmn", "&#x02216;");
        put("sqcap", "&#x02293;");
        put("sqcup", "&#x02294;");
        put("ssetmn", "&#x02216;");
        put("sstarf", "&#x022C6;");
        put("sum", "&#x02211;");
        put("timesb", "&#x022A0;");
        put("top", "&#x022A4;");
        put("uplus", "&#x0228E;");
        put("wreath", "&#x02240;");
        put("xcirc", "&#x025EF;");
        put("xdtri", "&#x025BD;");
        put("xutri", "&#x025B3;");
        put("dlcorn", "&#x0231E;");
        put("drcorn", "&#x0231F;");
        put("lceil", "&#x02308;");
        put("lfloor", "&#x0230A;");
        put("lpargt", "&#x029A0;");
        put("rceil", "&#x02309;");
        put("rfloor", "&#x0230B;");
        put("rpargt", "&#x02994;");
        put("ulcorn", "&#x0231C;");
        put("urcorn", "&#x0231D;");
        put("gnap", "&#x02A8A;");
        put("gnE", "&#x02269;");
        put("gne", "&#x02A88;");
        put("gnsim", "&#x022E7;");
        put("gvnE", "&#x02269;&#x0FE00;");
        put("lnap", "&#x02A89;");
        put("lnE", "&#x02268;");
        put("lne", "&#x02A87;");
        put("lnsim", "&#x022E6;");
        put("lvnE", "&#x02268;&#x0FE00;");
        put("nap", "&#x02249;");
        put("ncong", "&#x02247;");
        put("nequiv", "&#x02262;");
        put("ngE", "&#x02267;&#x00338;");
        put("nge", "&#x02271;");
        put("nges", "&#x02A7E;&#x00338;");
        put("ngt", "&#x0226F;");
        put("nlE", "&#x02266;&#x00338;");
        put("nle", "&#x02270;");
        put("nles", "&#x02A7D;&#x00338;");
        put("nlt", "&#x0226E;");
        put("nltri", "&#x022EA;");
        put("nltrie", "&#x022EC;");
        put("nmid", "&#x02224;");
        put("npar", "&#x02226;");
        put("npr", "&#x02280;");
        put("npre", "&#x02AAF;&#x00338;");
        put("nrtri", "&#x022EB;");
        put("nrtrie", "&#x022ED;");
        put("nsc", "&#x02281;");
        put("nsce", "&#x02AB0;&#x00338;");
        put("nsim", "&#x02241;");
        put("nsime", "&#x02244;");
        put("nsmid", "&#x02224;");
        put("nspar", "&#x02226;");
        put("nsub", "&#x02284;");
        put("nsubE", "&#x02AC5;&#x00338;");
        put("nsube", "&#x02288;");
        put("nsup", "&#x02285;");
        put("nsupE", "&#x02AC6;&#x00338;");
        put("nsupe", "&#x02289;");
        put("nVDash", "&#x022AF;");
        put("nVdash", "&#x022AE;");
        put("nvDash", "&#x022AD;");
        put("nvdash", "&#x022AC;");
        put("prnap", "&#x02AB9;");
        put("prnE", "&#x02AB5;");
        put("prnsim", "&#x022E8;");
        put("scnap", "&#x02ABA;");
        put("scnE", "&#x02AB6;");
        put("scnsim", "&#x022E9;");
        put("subnE", "&#x02ACB;");
        put("subne", "&#x0228A;");
        put("supnE", "&#x02ACC;");
        put("supne", "&#x0228B;");
        put("vsubnE", "&#x02ACB;&#x0FE00;");
        put("vsubne", "&#x0228A;&#x0FE00;");
        put("vsupnE", "&#x02ACC;&#x0FE00;");
        put("vsupne", "&#x0228B;&#x0FE00;");
        put("ang", "&#x02220;");
        put("angmsd", "&#x02221;");
        put("beth", "&#x02136;");
        put("bprime", "&#x02035;");
        put("comp", "&#x02201;");
        put("daleth", "&#x02138;");
        put("ell", "&#x02113;");
        put("empty", "&#x02205;");
        put("gimel", "&#x02137;");
        put("inodot", "&#x00131;");
        put("jnodot", "&#x0006A;");
        put("nexist", "&#x02204;");
        put("oS", "&#x024C8;");
        put("planck", "&#x0210F;");
        put("real", "&#x0211C;");
        put("sbsol", "&#x0FE68;");
        put("vprime", "&#x02032;");
        put("weierp", "&#x02118;");
        put("ape", "&#x0224A;");
        put("asymp", "&#x02248;");
        put("bcong", "&#x0224C;");
        put("bepsi", "&#x003F6;");
        put("bowtie", "&#x022C8;");
        put("bsim", "&#x0223D;");
        put("bsime", "&#x022CD;");
        put("bump", "&#x0224E;");
        put("bumpe", "&#x0224F;");
        put("cire", "&#x02257;");
        put("colone", "&#x02254;");
        put("cuepr", "&#x022DE;");
        put("cuesc", "&#x022DF;");
        put("cupre", "&#x0227C;");
        put("dashv", "&#x022A3;");
        put("ecir", "&#x02256;");
        put("ecolon", "&#x02255;");
        put("eDot", "&#x02251;");
        put("efDot", "&#x02252;");
        put("egs", "&#x02A96;");
        put("els", "&#x02A95;");
        put("erDot", "&#x02253;");
        put("esdot", "&#x02250;");
        put("fork", "&#x022D4;");
        put("frown", "&#x02322;");
        put("gap", "&#x02A86;");
        put("gE", "&#x02267;");
        put("gEl", "&#x02A8C;");
        put("gel", "&#x022DB;");
        put("ges", "&#x02A7E;");
        put("Gg", "&#x022D9;");
        put("gl", "&#x02277;");
        put("gsdot", "&#x022D7;");
        put("gsim", "&#x02273;");
        put("Gt", "&#x0226B;");
        put("lap", "&#x02A85;");
        put("ldot", "&#x022D6;");
        put("lE", "&#x02266;");
        put("lEg", "&#x02A8B;");
        put("leg", "&#x022DA;");
        put("les", "&#x02A7D;");
        put("lg", "&#x02276;");
        put("Ll", "&#x022D8;");
        put("lsim", "&#x02272;");
        put("Lt", "&#x0226A;");
        put("ltrie", "&#x022B4;");
        put("mid", "&#x02223;");
        put("models", "&#x022A7;");
        put("pr", "&#x0227A;");
        put("prap", "&#x02AB7;");
        put("pre", "&#x02AAF;");
        put("prsim", "&#x0227E;");
        put("rtrie", "&#x022B5;");
        put("samalg", "&#x02210;");
        put("sc", "&#x0227B;");
        put("scap", "&#x02AB8;");
        put("sccue", "&#x0227D;");
        put("sce", "&#x02AB0;");
        put("scsim", "&#x0227F;");
        put("sfrown", "&#x02322;");
        put("smid", "&#x02223;");
        put("smile", "&#x02323;");
        put("spar", "&#x02225;");
        put("sqsub", "&#x0228F;");
        put("sqsube", "&#x02291;");
        put("sqsup", "&#x02290;");
        put("sqsupe", "&#x02292;");
        put("ssmile", "&#x02323;");
        put("Sub", "&#x022D0;");
        put("subE", "&#x02AC5;");
        put("Sup", "&#x022D1;");
        put("supE", "&#x02AC6;");
        put("thkap", "&#x02248;");
        put("thksim", "&#x0223C;");
        put("trie", "&#x0225C;");
        put("twixt", "&#x0226C;");
        put("Vdash", "&#x022A9;");
        put("vDash", "&#x022A8;");
        put("vdash", "&#x022A2;");
        put("veebar", "&#x022BB;");
        put("vltri", "&#x022B2;");
        put("vprop", "&#x0221D;");
        put("vrtri", "&#x022B3;");
        put("Vvdash", "&#x022AA;");
        put("boxDL", "&#x02557;");
        put("boxDl", "&#x02556;");
        put("boxdL", "&#x02555;");
        put("boxdl", "&#x02510;");
        put("boxDR", "&#x02554;");
        put("boxDr", "&#x02553;");
        put("boxdR", "&#x02552;");
        put("boxdr", "&#x0250C;");
        put("boxH", "&#x02550;");
        put("boxh", "&#x02500;");
        put("boxHD", "&#x02566;");
        put("boxHd", "&#x02564;");
        put("boxhD", "&#x02565;");
        put("boxhd", "&#x0252C;");
        put("boxHU", "&#x02569;");
        put("boxHu", "&#x02567;");
        put("boxhU", "&#x02568;");
        put("boxhu", "&#x02534;");
        put("boxUL", "&#x0255D;");
        put("boxUl", "&#x0255C;");
        put("boxuL", "&#x0255B;");
        put("boxul", "&#x02518;");
        put("boxUR", "&#x0255A;");
        put("boxUr", "&#x02559;");
        put("boxuR", "&#x02558;");
        put("boxur", "&#x02514;");
        put("boxV", "&#x02551;");
        put("boxv", "&#x02502;");
        put("boxVH", "&#x0256C;");
        put("boxVh", "&#x0256B;");
        put("boxvH", "&#x0256A;");
        put("boxvh", "&#x0253C;");
        put("boxVL", "&#x02563;");
        put("boxVl", "&#x02562;");
        put("boxvL", "&#x02561;");
        put("boxvl", "&#x02524;");
        put("boxVR", "&#x02560;");
        put("boxVr", "&#x0255F;");
        put("boxvR", "&#x0255E;");
        put("boxvr", "&#x0251C;");
        put("Acy", "&#x00410;");
        put("acy", "&#x00430;");
        put("Bcy", "&#x00411;");
        put("bcy", "&#x00431;");
        put("CHcy", "&#x00427;");
        put("chcy", "&#x00447;");
        put("Dcy", "&#x00414;");
        put("dcy", "&#x00434;");
        put("Ecy", "&#x0042D;");
        put("ecy", "&#x0044D;");
        put("Fcy", "&#x00424;");
        put("fcy", "&#x00444;");
        put("Gcy", "&#x00413;");
        put("gcy", "&#x00433;");
        put("HARDcy", "&#x0042A;");
        put("hardcy", "&#x0044A;");
        put("Icy", "&#x00418;");
        put("icy", "&#x00438;");
        put("IEcy", "&#x00415;");
        put("iecy", "&#x00435;");
        put("IOcy", "&#x00401;");
        put("iocy", "&#x00451;");
        put("Jcy", "&#x00419;");
        put("jcy", "&#x00439;");
        put("Kcy", "&#x0041A;");
        put("kcy", "&#x0043A;");
        put("KHcy", "&#x00425;");
        put("khcy", "&#x00445;");
        put("Lcy", "&#x0041B;");
        put("lcy", "&#x0043B;");
        put("Mcy", "&#x0041C;");
        put("mcy", "&#x0043C;");
        put("Ncy", "&#x0041D;");
        put("ncy", "&#x0043D;");
        put("numero", "&#x02116;");
        put("Ocy", "&#x0041E;");
        put("ocy", "&#x0043E;");
        put("Pcy", "&#x0041F;");
        put("pcy", "&#x0043F;");
        put("Rcy", "&#x00420;");
        put("rcy", "&#x00440;");
        put("Scy", "&#x00421;");
        put("scy", "&#x00441;");
        put("SHCHcy", "&#x00429;");
        put("shchcy", "&#x00449;");
        put("SHcy", "&#x00428;");
        put("shcy", "&#x00448;");
        put("SOFTcy", "&#x0042C;");
        put("softcy", "&#x0044C;");
        put("Tcy", "&#x00422;");
        put("tcy", "&#x00442;");
        put("TScy", "&#x00426;");
        put("tscy", "&#x00446;");
        put("Ucy", "&#x00423;");
        put("ucy", "&#x00443;");
        put("Vcy", "&#x00412;");
        put("vcy", "&#x00432;");
        put("YAcy", "&#x0042F;");
        put("yacy", "&#x0044F;");
        put("Ycy", "&#x0042B;");
        put("ycy", "&#x0044B;");
        put("YUcy", "&#x0042E;");
        put("yucy", "&#x0044E;");
        put("Zcy", "&#x00417;");
        put("zcy", "&#x00437;");
        put("ZHcy", "&#x00416;");
        put("zhcy", "&#x00436;");
        put("DJcy", "&#x00402;");
        put("djcy", "&#x00452;");
        put("DScy", "&#x00405;");
        put("dscy", "&#x00455;");
        put("DZcy", "&#x0040F;");
        put("dzcy", "&#x0045F;");
        put("GJcy", "&#x00403;");
        put("gjcy", "&#x00453;");
        put("Iukcy", "&#x00406;");
        put("iukcy", "&#x00456;");
        put("Jsercy", "&#x00408;");
        put("jsercy", "&#x00458;");
        put("Jukcy", "&#x00404;");
        put("jukcy", "&#x00454;");
        put("KJcy", "&#x0040C;");
        put("kjcy", "&#x0045C;");
        put("LJcy", "&#x00409;");
        put("ljcy", "&#x00459;");
        put("NJcy", "&#x0040A;");
        put("njcy", "&#x0045A;");
        put("TSHcy", "&#x0040B;");
        put("tshcy", "&#x0045B;");
        put("Ubrcy", "&#x0040E;");
        put("ubrcy", "&#x0045E;");
        put("YIcy", "&#x00407;");
        put("yicy", "&#x00457;");
        put("acute", "&#x000B4;");
        put("breve", "&#x002D8;");
        put("caron", "&#x002C7;");
        put("cedil", "&#x000B8;");
        put("circ", "&#x002C6;");
        put("dblac", "&#x002DD;");
        put("die", "&#x000A8;");
        put("dot", "&#x002D9;");
        put("grave", "&#x00060;");
        put("macr", "&#x000AF;");
        put("ogon", "&#x002DB;");
        put("ring", "&#x002DA;");
        put("tilde", "&#x002DC;");
        put("uml", "&#x000A8;");
        put("Agr", "&#x00391;");
        put("agr", "&#x003B1;");
        put("Bgr", "&#x00392;");
        put("bgr", "&#x003B2;");
        put("Dgr", "&#x00394;");
        put("dgr", "&#x003B4;");
        put("EEgr", "&#x00397;");
        put("eegr", "&#x003B7;");
        put("Egr", "&#x00395;");
        put("egr", "&#x003B5;");
        put("Ggr", "&#x00393;");
        put("ggr", "&#x003B3;");
        put("Igr", "&#x00399;");
        put("igr", "&#x003B9;");
        put("Kgr", "&#x0039A;");
        put("kgr", "&#x003BA;");
        put("KHgr", "&#x003A7;");
        put("khgr", "&#x003C7;");
        put("Lgr", "&#x0039B;");
        put("lgr", "&#x003BB;");
        put("Mgr", "&#x0039C;");
        put("mgr", "&#x003BC;");
        put("Ngr", "&#x0039D;");
        put("ngr", "&#x003BD;");
        put("Ogr", "&#x0039F;");
        put("ogr", "&#x003BF;");
        put("OHgr", "&#x003A9;");
        put("ohgr", "&#x003C9;");
        put("Pgr", "&#x003A0;");
        put("pgr", "&#x003C0;");
        put("PHgr", "&#x003A6;");
        put("phgr", "&#x003C6;");
        put("PSgr", "&#x003A8;");
        put("psgr", "&#x003C8;");
        put("Rgr", "&#x003A1;");
        put("rgr", "&#x003C1;");
        put("sfgr", "&#x003C2;");
        put("Sgr", "&#x003A3;");
        put("sgr", "&#x003C3;");
        put("Tgr", "&#x003A4;");
        put("tgr", "&#x003C4;");
        put("THgr", "&#x00398;");
        put("thgr", "&#x003B8;");
        put("Ugr", "&#x003A5;");
        put("ugr", "&#x003C5;");
        put("Xgr", "&#x0039E;");
        put("xgr", "&#x003BE;");
        put("Zgr", "&#x00396;");
        put("zgr", "&#x003B6;");
        put("Aacgr", "&#x00386;");
        put("aacgr", "&#x003AC;");
        put("Eacgr", "&#x00388;");
        put("eacgr", "&#x003AD;");
        put("EEacgr", "&#x00389;");
        put("eeacgr", "&#x003AE;");
        put("Iacgr", "&#x0038A;");
        put("iacgr", "&#x003AF;");
        put("idiagr", "&#x00390;");
        put("Idigr", "&#x003AA;");
        put("idigr", "&#x003CA;");
        put("Oacgr", "&#x0038C;");
        put("oacgr", "&#x003CC;");
        put("OHacgr", "&#x0038F;");
        put("ohacgr", "&#x003CE;");
        put("Uacgr", "&#x0038E;");
        put("uacgr", "&#x003CD;");
        put("udiagr", "&#x003B0;");
        put("Udigr", "&#x003AB;");
        put("udigr", "&#x003CB;");
        put("alpha", "&#x003B1;");
        put("beta", "&#x003B2;");
        put("chi", "&#x003C7;");
        put("Delta", "&#x00394;");
        put("delta", "&#x003B4;");
        put("epsi", "&#x003F5;");
        put("epsis", "&#x003F5;");
        put("epsiv", "&#x003B5;");
        put("eta", "&#x003B7;");
        put("Gamma", "&#x00393;");
        put("gamma", "&#x003B3;");
        put("gammad", "&#x003DD;");
        put("iota", "&#x003B9;");
        put("kappa", "&#x003BA;");
        put("kappav", "&#x003F0;");
        put("Lambda", "&#x0039B;");
        put("lambda", "&#x003BB;");
        put("mu", "&#x003BC;");
        put("nu", "&#x003BD;");
        put("Omega", "&#x003A9;");
        put("omega", "&#x003C9;");
        put("Phi", "&#x003A6;");
        put("phis", "&#x003D5;");
        put("phiv", "&#x003C6;");
        put("Pi", "&#x003A0;");
        put("pi", "&#x003C0;");
        put("piv", "&#x003D6;");
        put("Psi", "&#x003A8;");
        put("psi", "&#x003C8;");
        put("rho", "&#x003C1;");
        put("rhov", "&#x003F1;");
        put("Sigma", "&#x003A3;");
        put("sigma", "&#x003C3;");
        put("sigmav", "&#x003C2;");
        put("tau", "&#x003C4;");
        put("Theta", "&#x00398;");
        put("thetas", "&#x003B8;");
        put("thetav", "&#x003D1;");
        put("Upsi", "&#x003D2;");
        put("upsi", "&#x003C5;");
        put("Xi", "&#x0039E;");
        put("xi", "&#x003BE;");
        put("zeta", "&#x003B6;");
        put("b.alpha", "&#x1D6C2;");
        put("b.beta", "&#x1D6C3;");
        put("b.chi", "&#x1D6D8;");
        put("b.Delta", "&#x1D6AB;");
        put("b.delta", "&#x1D6C5;");
        put("b.epsi", "&#x1D6C6;");
        put("b.epsiv", "&#x1D6DC;");
        put("b.eta", "&#x1D6C8;");
        put("b.Gamma", "&#x1D6AA;");
        put("b.gamma", "&#x1D6C4;");
        put("b.Gammad", "&#x003DC;");
        put("b.gammad", "&#x003DD;");
        put("b.iota", "&#x1D6CA;");
        put("b.kappa", "&#x1D6CB;");
        put("b.kappav", "&#x1D6DE;");
        put("b.Lambda", "&#x1D6B2;");
        put("b.lambda", "&#x1D6CC;");
        put("b.mu", "&#x1D6CD;");
        put("b.nu", "&#x1D6CE;");
        put("b.Omega", "&#x1D6C0;");
        put("b.omega", "&#x1D6DA;");
        put("b.Phi", "&#x1D6BD;");
        put("b.phi", "&#x1D6D7;");
        put("b.phiv", "&#x1D6DF;");
        put("b.Pi", "&#x1D6B7;");
        put("b.pi", "&#x1D6D1;");
        put("b.piv", "&#x1D6E1;");
        put("b.Psi", "&#x1D6BF;");
        put("b.psi", "&#x1D6D9;");
        put("b.rho", "&#x1D6D2;");
        put("b.rhov", "&#x1D6E0;");
        put("b.Sigma", "&#x1D6BA;");
        put("b.sigma", "&#x1D6D4;");
        put("b.sigmav", "&#x1D6D3;");
        put("b.tau", "&#x1D6D5;");
        put("b.Theta", "&#x1D6AF;");
        put("b.thetas", "&#x1D6C9;");
        put("b.thetav", "&#x1D6DD;");
        put("b.Upsi", "&#x1D6BC;");
        put("b.upsi", "&#x1D6D6;");
        put("b.Xi", "&#x1D6B5;");
        put("b.xi", "&#x1D6CF;");
        put("b.zeta", "&#x1D6C7;");
        put("Aacute", "&#x000C1;");
        put("aacute", "&#x000E1;");
        put("Acirc", "&#x000C2;");
        put("acirc", "&#x000E2;");
        put("AElig", "&#x000C6;");
        put("aelig", "&#x000E6;");
        put("Agrave", "&#x000C0;");
        put("agrave", "&#x000E0;");
        put("Aring", "&#x000C5;");
        put("aring", "&#x000E5;");
        put("Atilde", "&#x000C3;");
        put("atilde", "&#x000E3;");
        put("Auml", "&#x000C4;");
        put("auml", "&#x000E4;");
        put("Ccedil", "&#x000C7;");
        put("ccedil", "&#x000E7;");
        put("Eacute", "&#x000C9;");
        put("eacute", "&#x000E9;");
        put("Ecirc", "&#x000CA;");
        put("ecirc", "&#x000EA;");
        put("Egrave", "&#x000C8;");
        put("egrave", "&#x000E8;");
        put("ETH", "&#x000D0;");
        put("eth", "&#x000F0;");
        put("Euml", "&#x000CB;");
        put("euml", "&#x000EB;");
        put("Iacute", "&#x000CD;");
        put("iacute", "&#x000ED;");
        put("Icirc", "&#x000CE;");
        put("icirc", "&#x000EE;");
        put("Igrave", "&#x000CC;");
        put("igrave", "&#x000EC;");
        put("Iuml", "&#x000CF;");
        put("iuml", "&#x000EF;");
        put("Ntilde", "&#x000D1;");
        put("ntilde", "&#x000F1;");
        put("Oacute", "&#x000D3;");
        put("oacute", "&#x000F3;");
        put("Ocirc", "&#x000D4;");
        put("ocirc", "&#x000F4;");
        put("Ograve", "&#x000D2;");
        put("ograve", "&#x000F2;");
        put("Oslash", "&#x000D8;");
        put("oslash", "&#x000F8;");
        put("Otilde", "&#x000D5;");
        put("otilde", "&#x000F5;");
        put("Ouml", "&#x000D6;");
        put("ouml", "&#x000F6;");
        put("szlig", "&#x000DF;");
        put("THORN", "&#x000DE;");
        put("thorn", "&#x000FE;");
        put("Uacute", "&#x000DA;");
        put("uacute", "&#x000FA;");
        put("Ucirc", "&#x000DB;");
        put("ucirc", "&#x000FB;");
        put("Ugrave", "&#x000D9;");
        put("ugrave", "&#x000F9;");
        put("Uuml", "&#x000DC;");
        put("uuml", "&#x000FC;");
        put("Yacute", "&#x000DD;");
        put("yacute", "&#x000FD;");
        put("yuml", "&#x000FF;");
        put("Abreve", "&#x00102;");
        put("abreve", "&#x00103;");
        put("Amacr", "&#x00100;");
        put("amacr", "&#x00101;");
        put("Aogon", "&#x00104;");
        put("aogon", "&#x00105;");
        put("Cacute", "&#x00106;");
        put("cacute", "&#x00107;");
        put("Ccaron", "&#x0010C;");
        put("ccaron", "&#x0010D;");
        put("Ccirc", "&#x00108;");
        put("ccirc", "&#x00109;");
        put("Cdot", "&#x0010A;");
        put("cdot", "&#x0010B;");
        put("Dcaron", "&#x0010E;");
        put("dcaron", "&#x0010F;");
        put("Dstrok", "&#x00110;");
        put("dstrok", "&#x00111;");
        put("Ecaron", "&#x0011A;");
        put("ecaron", "&#x0011B;");
        put("Edot", "&#x00116;");
        put("edot", "&#x00117;");
        put("Emacr", "&#x00112;");
        put("emacr", "&#x00113;");
        put("ENG", "&#x0014A;");
        put("eng", "&#x0014B;");
        put("Eogon", "&#x00118;");
        put("eogon", "&#x00119;");
        put("gacute", "&#x001F5;");
        put("Gbreve", "&#x0011E;");
        put("gbreve", "&#x0011F;");
        put("Gcedil", "&#x00122;");
        put("Gcirc", "&#x0011C;");
        put("gcirc", "&#x0011D;");
        put("Gdot", "&#x00120;");
        put("gdot", "&#x00121;");
        put("Hcirc", "&#x00124;");
        put("hcirc", "&#x00125;");
        put("Hstrok", "&#x00126;");
        put("hstrok", "&#x00127;");
        put("Idot", "&#x00130;");
        put("IJlig", "&#x00132;");
        put("ijlig", "&#x00133;");
        put("Imacr", "&#x0012A;");
        put("imacr", "&#x0012B;");
        put("inodot", "&#x00131;");
        put("Iogon", "&#x0012E;");
        put("iogon", "&#x0012F;");
        put("Itilde", "&#x00128;");
        put("itilde", "&#x00129;");
        put("Jcirc", "&#x00134;");
        put("jcirc", "&#x00135;");
        put("Kcedil", "&#x00136;");
        put("kcedil", "&#x00137;");
        put("kgreen", "&#x00138;");
        put("Lacute", "&#x00139;");
        put("lacute", "&#x0013A;");
        put("Lcaron", "&#x0013D;");
        put("lcaron", "&#x0013E;");
        put("Lcedil", "&#x0013B;");
        put("lcedil", "&#x0013C;");
        put("Lmidot", "&#x0013F;");
        put("lmidot", "&#x00140;");
        put("Lstrok", "&#x00141;");
        put("lstrok", "&#x00142;");
        put("Nacute", "&#x00143;");
        put("nacute", "&#x00144;");
        put("napos", "&#x00149;");
        put("Ncaron", "&#x00147;");
        put("ncaron", "&#x00148;");
        put("Ncedil", "&#x00145;");
        put("ncedil", "&#x00146;");
        put("Odblac", "&#x00150;");
        put("odblac", "&#x00151;");
        put("OElig", "&#x00152;");
        put("oelig", "&#x00153;");
        put("Omacr", "&#x0014C;");
        put("omacr", "&#x0014D;");
        put("Racute", "&#x00154;");
        put("racute", "&#x00155;");
        put("Rcaron", "&#x00158;");
        put("rcaron", "&#x00159;");
        put("Rcedil", "&#x00156;");
        put("rcedil", "&#x00157;");
        put("Sacute", "&#x0015A;");
        put("sacute", "&#x0015B;");
        put("Scaron", "&#x00160;");
        put("scaron", "&#x00161;");
        put("Scedil", "&#x0015E;");
        put("scedil", "&#x0015F;");
        put("Scirc", "&#x0015C;");
        put("scirc", "&#x0015D;");
        put("Tcaron", "&#x00164;");
        put("tcaron", "&#x00165;");
        put("Tcedil", "&#x00162;");
        put("tcedil", "&#x00163;");
        put("Tstrok", "&#x00166;");
        put("tstrok", "&#x00167;");
        put("Ubreve", "&#x0016C;");
        put("ubreve", "&#x0016D;");
        put("Udblac", "&#x00170;");
        put("udblac", "&#x00171;");
        put("Umacr", "&#x0016A;");
        put("umacr", "&#x0016B;");
        put("Uogon", "&#x00172;");
        put("uogon", "&#x00173;");
        put("Uring", "&#x0016E;");
        put("uring", "&#x0016F;");
        put("Utilde", "&#x00168;");
        put("utilde", "&#x00169;");
        put("Wcirc", "&#x00174;");
        put("wcirc", "&#x00175;");
        put("Ycirc", "&#x00176;");
        put("ycirc", "&#x00177;");
        put("Yuml", "&#x00178;");
        put("Zacute", "&#x00179;");
        put("zacute", "&#x0017A;");
        put("Zcaron", "&#x0017D;");
        put("zcaron", "&#x0017E;");
        put("Zdot", "&#x0017B;");
        put("zdot", "&#x0017C;");
        put("amp", "&#38;#38;");
        put("apos", "&#x00027;");
        put("ast", "&#x0002A;");
        put("brvbar", "&#x000A6;");
        put("bsol", "&#x0005C;");
        put("cent", "&#x000A2;");
        put("colon", "&#x0003A;");
        put("comma", "&#x0002C;");
        put("commat", "&#x00040;");
        put("copy", "&#x000A9;");
        put("curren", "&#x000A4;");
        put("darr", "&#x02193;");
        put("deg", "&#x000B0;");
        put("divide", "&#x000F7;");
        put("dollar", "&#x00024;");
        put("equals", "&#x0003D;");
        put("excl", "&#x00021;");
        put("frac12", "&#x000BD;");
        put("frac14", "&#x000BC;");
        put("frac18", "&#x0215B;");
        put("frac34", "&#x000BE;");
        put("frac38", "&#x0215C;");
        put("frac58", "&#x0215D;");
        put("frac78", "&#x0215E;");
        put("gt", "&#x0003E;");
        put("half", "&#x000BD;");
        put("horbar", "&#x02015;");
        put("hyphen", "&#x02010;");
        put("iexcl", "&#x000A1;");
        put("iquest", "&#x000BF;");
        put("laquo", "&#x000AB;");
        put("larr", "&#x02190;");
        put("lcub", "&#x0007B;");
        put("ldquo", "&#x0201C;");
        put("lowbar", "&#x0005F;");
        put("lpar", "&#x00028;");
        put("lsqb", "&#x0005B;");
        put("lsquo", "&#x02018;");
        put("lt", "&#38;#60;");
        put("micro", "&#x000B5;");
        put("middot", "&#x000B7;");
        put("nbsp", "&#x000A0;");
        put("not", "&#x000AC;");
        put("num", "&#x00023;");
        put("ohm", "&#x02126;");
        put("ordf", "&#x000AA;");
        put("ordm", "&#x000BA;");
        put("para", "&#x000B6;");
        put("percnt", "&#x00025;");
        put("period", "&#x0002E;");
        put("plus", "&#x0002B;");
        put("plusmn", "&#x000B1;");
        put("pound", "&#x000A3;");
        put("quest", "&#x0003F;");
        put("quot", "&#x00022;");
        put("raquo", "&#x000BB;");
        put("rarr", "&#x02192;");
        put("rcub", "&#x0007D;");
        put("rdquo", "&#x0201D;");
        put("reg", "&#x000AE;");
        put("rpar", "&#x00029;");
        put("rsqb", "&#x0005D;");
        put("rsquo", "&#x02019;");
        put("sect", "&#x000A7;");
        put("semi", "&#x0003B;");
        put("shy", "&#x000AD;");
        put("sol", "&#x0002F;");
        put("sung", "&#x0266A;");
        put("sup1", "&#x000B9;");
        put("sup2", "&#x000B2;");
        put("sup3", "&#x000B3;");
        put("times", "&#x000D7;");
        put("trade", "&#x02122;");
        put("uarr", "&#x02191;");
        put("verbar", "&#x0007C;");
        put("yen", "&#x000A5;");
        put("blank", "&#x02423;");
        put("blk12", "&#x02592;");
        put("blk14", "&#x02591;");
        put("blk34", "&#x02593;");
        put("block", "&#x02588;");
        put("bull", "&#x02022;");
        put("caret", "&#x02041;");
        put("check", "&#x02713;");
        put("cir", "&#x025CB;");
        put("clubs", "&#x02663;");
        put("copysr", "&#x02117;");
        put("cross", "&#x02717;");
        put("Dagger", "&#x02021;");
        put("dagger", "&#x02020;");
        put("dash", "&#x02010;");
        put("diams", "&#x02666;");
        put("dlcrop", "&#x0230D;");
        put("drcrop", "&#x0230C;");
        put("dtri", "&#x025BF;");
        put("dtrif", "&#x025BE;");
        put("emsp", "&#x02003;");
        put("emsp13", "&#x02004;");
        put("emsp14", "&#x02005;");
        put("ensp", "&#x02002;");
        put("female", "&#x02640;");
        put("ffilig", "&#x0FB03;");
        put("fflig", "&#x0FB00;");
        put("ffllig", "&#x0FB04;");
        put("filig", "&#x0FB01;");
        put("flat", "&#x0266D;");
        put("fllig", "&#x0FB02;");
        put("frac13", "&#x02153;");
        put("frac15", "&#x02155;");
        put("frac16", "&#x02159;");
        put("frac23", "&#x02154;");
        put("frac25", "&#x02156;");
        put("frac35", "&#x02157;");
        put("frac45", "&#x02158;");
        put("frac56", "&#x0215A;");
        put("hairsp", "&#x0200A;");
        put("hearts", "&#x02665;");
        put("hellip", "&#x02026;");
        put("hybull", "&#x02043;");
        put("incare", "&#x02105;");
        put("ldquor", "&#x0201E;");
        put("lhblk", "&#x02584;");
        put("loz", "&#x025CA;");
        put("lozf", "&#x029EB;");
        put("lsquor", "&#x0201A;");
        put("ltri", "&#x025C3;");
        put("ltrif", "&#x025C2;");
        put("male", "&#x02642;");
        put("malt", "&#x02720;");
        put("marker", "&#x025AE;");
        put("mdash", "&#x02014;");
        put("mldr", "&#x02026;");
        put("natur", "&#x0266E;");
        put("ndash", "&#x02013;");
        put("nldr", "&#x02025;");
        put("numsp", "&#x02007;");
        put("phone", "&#x0260E;");
        put("puncsp", "&#x02008;");
        put("rdquor", "&#x0201D;");
        put("rect", "&#x025AD;");
        put("rsquor", "&#x02019;");
        put("rtri", "&#x025B9;");
        put("rtrif", "&#x025B8;");
        put("rx", "&#x0211E;");
        put("sext", "&#x02736;");
        put("sharp", "&#x0266F;");
        put("spades", "&#x02660;");
        put("squ", "&#x025A1;");
        put("squf", "&#x025AA;");
        put("star", "&#x02606;");
        put("starf", "&#x02605;");
        put("target", "&#x02316;");
        put("telrec", "&#x02315;");
        put("thinsp", "&#x02009;");
        put("uhblk", "&#x02580;");
        put("ulcrop", "&#x0230F;");
        put("urcrop", "&#x0230E;");
        put("utri", "&#x025B5;");
        put("utrif", "&#x025B4;");
        put("vellip", "&#x022EE;");
        put("aleph", "&#x02135;");
        put("and", "&#x02227;");
        put("ang90", "&#x0221F;");
        put("angsph", "&#x02222;");
        put("angst", "&#x0212B;");
        put("ap", "&#x02248;");
        put("becaus", "&#x02235;");
        put("bernou", "&#x0212C;");
        put("bottom", "&#x022A5;");
        put("cap", "&#x02229;");
        put("compfn", "&#x02218;");
        put("cong", "&#x02245;");
        put("conint", "&#x0222E;");
        put("cup", "&#x0222A;");
        put("Dot", "&#x000A8;");
        put("DotDot", " &#x020DC;");
        put("equiv", "&#x02261;");
        put("exist", "&#x02203;");
        put("fnof", "&#x00192;");
        put("forall", "&#x02200;");
        put("ge", "&#x02265;");
        put("hamilt", "&#x0210B;");
        put("iff", "&#x021D4;");
        put("infin", "&#x0221E;");
        put("int", "&#x0222B;");
        put("isin", "&#x02208;");
        put("lagran", "&#x02112;");
        put("lang", "&#x02329;");
        put("lArr", "&#x021D0;");
        put("le", "&#x02264;");
        put("lowast", "&#x02217;");
        put("minus", "&#x02212;");
        put("mnplus", "&#x02213;");
        put("nabla", "&#x02207;");
        put("ne", "&#x02260;");
        put("ni", "&#x0220B;");
        put("notin", "&#x02209;");
        put("or", "&#x02228;");
        put("order", "&#x02134;");
        put("par", "&#x02225;");
        put("part", "&#x02202;");
        put("permil", "&#x02030;");
        put("perp", "&#x022A5;");
        put("phmmat", "&#x02133;");
        put("Prime", "&#x02033;");
        put("prime", "&#x02032;");
        put("prop", "&#x0221D;");
        put("radic", "&#x0221A;");
        put("rang", "&#x0232A;");
        put("rArr", "&#x021D2;");
        put("sim", "&#x0223C;");
        put("sime", "&#x02243;");
        put("square", "&#x025A1;");
        put("sub", "&#x02282;");
        put("sube", "&#x02286;");
        put("sup", "&#x02283;");
        put("supe", "&#x02287;");
        put("tdot", " &#x020DB;");
        put("there4", "&#x02234;");
        put("tprime", "&#x02034;");
        put("Verbar", "&#x02016;");
        put("wedgeq", "&#x02259;");
    }};

    public static final String DOCBOOK_ENTITIES_STRING = "<!ENTITY euro \"&#x20AC;\">\n" +
            "<!ENTITY cularr \"&#x021B6;\">\n" +
            "<!ENTITY curarr \"&#x021B7;\">\n" +
            "<!ENTITY dArr \"&#x021D3;\">\n" +
            "<!ENTITY darr2 \"&#x021CA;\">\n" +
            "<!ENTITY dharl \"&#x021C3;\">\n" +
            "<!ENTITY dharr \"&#x021C2;\">\n" +
            "<!ENTITY dlarr \"&#x02199;\">\n" +
            "<!ENTITY drarr \"&#x02198;\">\n" +
            "<!ENTITY hArr \"&#x021D4;\">\n" +
            "<!ENTITY harr \"&#x02194;\">\n" +
            "<!ENTITY harrw \"&#x021AD;\">\n" +
            "<!ENTITY lAarr \"&#x021DA;\">\n" +
            "<!ENTITY Larr \"&#x0219E;\">\n" +
            "<!ENTITY larr2 \"&#x021C7;\">\n" +
            "<!ENTITY larrhk \"&#x021A9;\">\n" +
            "<!ENTITY larrlp \"&#x021AB;\">\n" +
            "<!ENTITY larrtl \"&#x021A2;\">\n" +
            "<!ENTITY lhard \"&#x021BD;\">\n" +
            "<!ENTITY lharu \"&#x021BC;\">\n" +
            "<!ENTITY lrarr2 \"&#x021C6;\">\n" +
            "<!ENTITY lrhar2 \"&#x021CB;\">\n" +
            "<!ENTITY lsh \"&#x021B0;\">\n" +
            "<!ENTITY map \"&#x021A6;\">\n" +
            "<!ENTITY mumap \"&#x022B8;\">\n" +
            "<!ENTITY nearr \"&#x02197;\">\n" +
            "<!ENTITY nhArr \"&#x021CE;\">\n" +
            "<!ENTITY nharr \"&#x021AE;\">\n" +
            "<!ENTITY nlArr \"&#x021CD;\">\n" +
            "<!ENTITY nlarr \"&#x0219A;\">\n" +
            "<!ENTITY nrArr \"&#x021CF;\">\n" +
            "<!ENTITY nrarr \"&#x0219B;\">\n" +
            "<!ENTITY nwarr \"&#x02196;\">\n" +
            "<!ENTITY olarr \"&#x021BA;\">\n" +
            "<!ENTITY orarr \"&#x021BB;\">\n" +
            "<!ENTITY rAarr \"&#x021DB;\">\n" +
            "<!ENTITY Rarr \"&#x021A0;\">\n" +
            "<!ENTITY rarr2 \"&#x021C9;\">\n" +
            "<!ENTITY rarrhk \"&#x021AA;\">\n" +
            "<!ENTITY rarrlp \"&#x021AC;\">\n" +
            "<!ENTITY rarrtl \"&#x021A3;\">\n" +
            "<!ENTITY rarrw \"&#x0219D;\">\n" +
            "<!ENTITY rhard \"&#x021C1;\">\n" +
            "<!ENTITY rharu \"&#x021C0;\">\n" +
            "<!ENTITY rlarr2 \"&#x021C4;\">\n" +
            "<!ENTITY rlhar2 \"&#x021CC;\">\n" +
            "<!ENTITY rsh \"&#x021B1;\">\n" +
            "<!ENTITY uArr \"&#x021D1;\">\n" +
            "<!ENTITY uarr2 \"&#x021C8;\">\n" +
            "<!ENTITY uharl \"&#x021BF;\">\n" +
            "<!ENTITY uharr \"&#x021BE;\">\n" +
            "<!ENTITY vArr \"&#x021D5;\">\n" +
            "<!ENTITY varr \"&#x02195;\">\n" +
            "<!ENTITY xhArr \"&#x027FA;\">\n" +
            "<!ENTITY xharr \"&#x027F7;\">\n" +
            "<!ENTITY xlArr \"&#x027F8;\">\n" +
            "<!ENTITY xrArr \"&#x027F9;\">\n" +
            "<!ENTITY amalg \"&#x02A3F;\">\n" +
            "<!ENTITY Barwed \"&#x02306;\">\n" +
            "<!ENTITY barwed \"&#x02305;\">\n" +
            "<!ENTITY Cap \"&#x022D2;\">\n" +
            "<!ENTITY coprod \"&#x02210;\">\n" +
            "<!ENTITY Cup \"&#x022D3;\">\n" +
            "<!ENTITY cuvee \"&#x022CE;\">\n" +
            "<!ENTITY cuwed \"&#x022CF;\">\n" +
            "<!ENTITY diam \"&#x022C4;\">\n" +
            "<!ENTITY divonx \"&#x022C7;\">\n" +
            "<!ENTITY intcal \"&#x022BA;\">\n" +
            "<!ENTITY lthree \"&#x022CB;\">\n" +
            "<!ENTITY ltimes \"&#x022C9;\">\n" +
            "<!ENTITY minusb \"&#x0229F;\">\n" +
            "<!ENTITY oast \"&#x0229B;\">\n" +
            "<!ENTITY ocir \"&#x0229A;\">\n" +
            "<!ENTITY odash \"&#x0229D;\">\n" +
            "<!ENTITY odot \"&#x02299;\">\n" +
            "<!ENTITY ominus \"&#x02296;\">\n" +
            "<!ENTITY oplus \"&#x02295;\">\n" +
            "<!ENTITY osol \"&#x02298;\">\n" +
            "<!ENTITY otimes \"&#x02297;\">\n" +
            "<!ENTITY plusb \"&#x0229E;\">\n" +
            "<!ENTITY plusdo \"&#x02214;\">\n" +
            "<!ENTITY prod \"&#x0220F;\">\n" +
            "<!ENTITY rthree \"&#x022CC;\">\n" +
            "<!ENTITY rtimes \"&#x022CA;\">\n" +
            "<!ENTITY sdot \"&#x022C5;\">\n" +
            "<!ENTITY sdotb \"&#x022A1;\">\n" +
            "<!ENTITY setmn \"&#x02216;\">\n" +
            "<!ENTITY sqcap \"&#x02293;\">\n" +
            "<!ENTITY sqcup \"&#x02294;\">\n" +
            "<!ENTITY ssetmn \"&#x02216;\">\n" +
            "<!ENTITY sstarf \"&#x022C6;\">\n" +
            "<!ENTITY sum \"&#x02211;\">\n" +
            "<!ENTITY timesb \"&#x022A0;\">\n" +
            "<!ENTITY top \"&#x022A4;\">\n" +
            "<!ENTITY uplus \"&#x0228E;\">\n" +
            "<!ENTITY wreath \"&#x02240;\">\n" +
            "<!ENTITY xcirc \"&#x025EF;\">\n" +
            "<!ENTITY xdtri \"&#x025BD;\">\n" +
            "<!ENTITY xutri \"&#x025B3;\">\n" +
            "<!ENTITY dlcorn \"&#x0231E;\">\n" +
            "<!ENTITY drcorn \"&#x0231F;\">\n" +
            "<!ENTITY lceil \"&#x02308;\">\n" +
            "<!ENTITY lfloor \"&#x0230A;\">\n" +
            "<!ENTITY lpargt \"&#x029A0;\">\n" +
            "<!ENTITY rceil \"&#x02309;\">\n" +
            "<!ENTITY rfloor \"&#x0230B;\">\n" +
            "<!ENTITY rpargt \"&#x02994;\">\n" +
            "<!ENTITY ulcorn \"&#x0231C;\">\n" +
            "<!ENTITY urcorn \"&#x0231D;\">\n" +
            "<!ENTITY gnap \"&#x02A8A;\">\n" +
            "<!ENTITY gnE \"&#x02269;\">\n" +
            "<!ENTITY gne \"&#x02A88;\">\n" +
            "<!ENTITY gnsim \"&#x022E7;\">\n" +
            "<!ENTITY gvnE \"&#x02269;&#x0FE00;\">\n" +
            "<!ENTITY lnap \"&#x02A89;\">\n" +
            "<!ENTITY lnE \"&#x02268;\">\n" +
            "<!ENTITY lne \"&#x02A87;\">\n" +
            "<!ENTITY lnsim \"&#x022E6;\">\n" +
            "<!ENTITY lvnE \"&#x02268;&#x0FE00;\">\n" +
            "<!ENTITY nap \"&#x02249;\">\n" +
            "<!ENTITY ncong \"&#x02247;\">\n" +
            "<!ENTITY nequiv \"&#x02262;\">\n" +
            "<!ENTITY ngE \"&#x02267;&#x00338;\">\n" +
            "<!ENTITY nge \"&#x02271;\">\n" +
            "<!ENTITY nges \"&#x02A7E;&#x00338;\">\n" +
            "<!ENTITY ngt \"&#x0226F;\">\n" +
            "<!ENTITY nlE \"&#x02266;&#x00338;\">\n" +
            "<!ENTITY nle \"&#x02270;\">\n" +
            "<!ENTITY nles \"&#x02A7D;&#x00338;\">\n" +
            "<!ENTITY nlt \"&#x0226E;\">\n" +
            "<!ENTITY nltri \"&#x022EA;\">\n" +
            "<!ENTITY nltrie \"&#x022EC;\">\n" +
            "<!ENTITY nmid \"&#x02224;\">\n" +
            "<!ENTITY npar \"&#x02226;\">\n" +
            "<!ENTITY npr \"&#x02280;\">\n" +
            "<!ENTITY npre \"&#x02AAF;&#x00338;\">\n" +
            "<!ENTITY nrtri \"&#x022EB;\">\n" +
            "<!ENTITY nrtrie \"&#x022ED;\">\n" +
            "<!ENTITY nsc \"&#x02281;\">\n" +
            "<!ENTITY nsce \"&#x02AB0;&#x00338;\">\n" +
            "<!ENTITY nsim \"&#x02241;\">\n" +
            "<!ENTITY nsime \"&#x02244;\">\n" +
            "<!ENTITY nsmid \"&#x02224;\">\n" +
            "<!ENTITY nspar \"&#x02226;\">\n" +
            "<!ENTITY nsub \"&#x02284;\">\n" +
            "<!ENTITY nsubE \"&#x02AC5;&#x00338;\">\n" +
            "<!ENTITY nsube \"&#x02288;\">\n" +
            "<!ENTITY nsup \"&#x02285;\">\n" +
            "<!ENTITY nsupE \"&#x02AC6;&#x00338;\">\n" +
            "<!ENTITY nsupe \"&#x02289;\">\n" +
            "<!ENTITY nVDash \"&#x022AF;\">\n" +
            "<!ENTITY nVdash \"&#x022AE;\">\n" +
            "<!ENTITY nvDash \"&#x022AD;\">\n" +
            "<!ENTITY nvdash \"&#x022AC;\">\n" +
            "<!ENTITY prnap \"&#x02AB9;\">\n" +
            "<!ENTITY prnE \"&#x02AB5;\">\n" +
            "<!ENTITY prnsim \"&#x022E8;\">\n" +
            "<!ENTITY scnap \"&#x02ABA;\">\n" +
            "<!ENTITY scnE \"&#x02AB6;\">\n" +
            "<!ENTITY scnsim \"&#x022E9;\">\n" +
            "<!ENTITY subnE \"&#x02ACB;\">\n" +
            "<!ENTITY subne \"&#x0228A;\">\n" +
            "<!ENTITY supnE \"&#x02ACC;\">\n" +
            "<!ENTITY supne \"&#x0228B;\">\n" +
            "<!ENTITY vsubnE \"&#x02ACB;&#x0FE00;\">\n" +
            "<!ENTITY vsubne \"&#x0228A;&#x0FE00;\">\n" +
            "<!ENTITY vsupnE \"&#x02ACC;&#x0FE00;\">\n" +
            "<!ENTITY vsupne \"&#x0228B;&#x0FE00;\">\n" +
            "<!ENTITY ang \"&#x02220;\">\n" +
            "<!ENTITY angmsd \"&#x02221;\">\n" +
            "<!ENTITY beth \"&#x02136;\">\n" +
            "<!ENTITY bprime \"&#x02035;\">\n" +
            "<!ENTITY comp \"&#x02201;\">\n" +
            "<!ENTITY daleth \"&#x02138;\">\n" +
            "<!ENTITY ell \"&#x02113;\">\n" +
            "<!ENTITY empty \"&#x02205;\">\n" +
            "<!ENTITY gimel \"&#x02137;\">\n" +
            "<!ENTITY inodot \"&#x00131;\">\n" +
            "<!ENTITY jnodot \"&#x0006A;\">\n" +
            "<!ENTITY nexist \"&#x02204;\">\n" +
            "<!ENTITY oS \"&#x024C8;\">\n" +
            "<!ENTITY planck \"&#x0210F;\">\n" +
            "<!ENTITY real \"&#x0211C;\">\n" +
            "<!ENTITY sbsol \"&#x0FE68;\">\n" +
            "<!ENTITY vprime \"&#x02032;\">\n" +
            "<!ENTITY weierp \"&#x02118;\">\n" +
            "<!ENTITY ape \"&#x0224A;\">\n" +
            "<!ENTITY asymp \"&#x02248;\">\n" +
            "<!ENTITY bcong \"&#x0224C;\">\n" +
            "<!ENTITY bepsi \"&#x003F6;\">\n" +
            "<!ENTITY bowtie \"&#x022C8;\">\n" +
            "<!ENTITY bsim \"&#x0223D;\">\n" +
            "<!ENTITY bsime \"&#x022CD;\">\n" +
            "<!ENTITY bump \"&#x0224E;\">\n" +
            "<!ENTITY bumpe \"&#x0224F;\">\n" +
            "<!ENTITY cire \"&#x02257;\">\n" +
            "<!ENTITY colone \"&#x02254;\">\n" +
            "<!ENTITY cuepr \"&#x022DE;\">\n" +
            "<!ENTITY cuesc \"&#x022DF;\">\n" +
            "<!ENTITY cupre \"&#x0227C;\">\n" +
            "<!ENTITY dashv \"&#x022A3;\">\n" +
            "<!ENTITY ecir \"&#x02256;\">\n" +
            "<!ENTITY ecolon \"&#x02255;\">\n" +
            "<!ENTITY eDot \"&#x02251;\">\n" +
            "<!ENTITY efDot \"&#x02252;\">\n" +
            "<!ENTITY egs \"&#x02A96;\">\n" +
            "<!ENTITY els \"&#x02A95;\">\n" +
            "<!ENTITY erDot \"&#x02253;\">\n" +
            "<!ENTITY esdot \"&#x02250;\">\n" +
            "<!ENTITY fork \"&#x022D4;\">\n" +
            "<!ENTITY frown \"&#x02322;\">\n" +
            "<!ENTITY gap \"&#x02A86;\">\n" +
            "<!ENTITY gE \"&#x02267;\">\n" +
            "<!ENTITY gEl \"&#x02A8C;\">\n" +
            "<!ENTITY gel \"&#x022DB;\">\n" +
            "<!ENTITY ges \"&#x02A7E;\">\n" +
            "<!ENTITY Gg \"&#x022D9;\">\n" +
            "<!ENTITY gl \"&#x02277;\">\n" +
            "<!ENTITY gsdot \"&#x022D7;\">\n" +
            "<!ENTITY gsim \"&#x02273;\">\n" +
            "<!ENTITY Gt \"&#x0226B;\">\n" +
            "<!ENTITY lap \"&#x02A85;\">\n" +
            "<!ENTITY ldot \"&#x022D6;\">\n" +
            "<!ENTITY lE \"&#x02266;\">\n" +
            "<!ENTITY lEg \"&#x02A8B;\">\n" +
            "<!ENTITY leg \"&#x022DA;\">\n" +
            "<!ENTITY les \"&#x02A7D;\">\n" +
            "<!ENTITY lg \"&#x02276;\">\n" +
            "<!ENTITY Ll \"&#x022D8;\">\n" +
            "<!ENTITY lsim \"&#x02272;\">\n" +
            "<!ENTITY Lt \"&#x0226A;\">\n" +
            "<!ENTITY ltrie \"&#x022B4;\">\n" +
            "<!ENTITY mid \"&#x02223;\">\n" +
            "<!ENTITY models \"&#x022A7;\">\n" +
            "<!ENTITY pr \"&#x0227A;\">\n" +
            "<!ENTITY prap \"&#x02AB7;\">\n" +
            "<!ENTITY pre \"&#x02AAF;\">\n" +
            "<!ENTITY prsim \"&#x0227E;\">\n" +
            "<!ENTITY rtrie \"&#x022B5;\">\n" +
            "<!ENTITY samalg \"&#x02210;\">\n" +
            "<!ENTITY sc \"&#x0227B;\">\n" +
            "<!ENTITY scap \"&#x02AB8;\">\n" +
            "<!ENTITY sccue \"&#x0227D;\">\n" +
            "<!ENTITY sce \"&#x02AB0;\">\n" +
            "<!ENTITY scsim \"&#x0227F;\">\n" +
            "<!ENTITY sfrown \"&#x02322;\">\n" +
            "<!ENTITY smid \"&#x02223;\">\n" +
            "<!ENTITY smile \"&#x02323;\">\n" +
            "<!ENTITY spar \"&#x02225;\">\n" +
            "<!ENTITY sqsub \"&#x0228F;\">\n" +
            "<!ENTITY sqsube \"&#x02291;\">\n" +
            "<!ENTITY sqsup \"&#x02290;\">\n" +
            "<!ENTITY sqsupe \"&#x02292;\">\n" +
            "<!ENTITY ssmile \"&#x02323;\">\n" +
            "<!ENTITY Sub \"&#x022D0;\">\n" +
            "<!ENTITY subE \"&#x02AC5;\">\n" +
            "<!ENTITY Sup \"&#x022D1;\">\n" +
            "<!ENTITY supE \"&#x02AC6;\">\n" +
            "<!ENTITY thkap \"&#x02248;\">\n" +
            "<!ENTITY thksim \"&#x0223C;\">\n" +
            "<!ENTITY trie \"&#x0225C;\">\n" +
            "<!ENTITY twixt \"&#x0226C;\">\n" +
            "<!ENTITY Vdash \"&#x022A9;\">\n" +
            "<!ENTITY vDash \"&#x022A8;\">\n" +
            "<!ENTITY vdash \"&#x022A2;\">\n" +
            "<!ENTITY veebar \"&#x022BB;\">\n" +
            "<!ENTITY vltri \"&#x022B2;\">\n" +
            "<!ENTITY vprop \"&#x0221D;\">\n" +
            "<!ENTITY vrtri \"&#x022B3;\">\n" +
            "<!ENTITY Vvdash \"&#x022AA;\">\n" +
            "<!ENTITY boxDL \"&#x02557;\">\n" +
            "<!ENTITY boxDl \"&#x02556;\">\n" +
            "<!ENTITY boxdL \"&#x02555;\">\n" +
            "<!ENTITY boxdl \"&#x02510;\">\n" +
            "<!ENTITY boxDR \"&#x02554;\">\n" +
            "<!ENTITY boxDr \"&#x02553;\">\n" +
            "<!ENTITY boxdR \"&#x02552;\">\n" +
            "<!ENTITY boxdr \"&#x0250C;\">\n" +
            "<!ENTITY boxH \"&#x02550;\">\n" +
            "<!ENTITY boxh \"&#x02500;\">\n" +
            "<!ENTITY boxHD \"&#x02566;\">\n" +
            "<!ENTITY boxHd \"&#x02564;\">\n" +
            "<!ENTITY boxhD \"&#x02565;\">\n" +
            "<!ENTITY boxhd \"&#x0252C;\">\n" +
            "<!ENTITY boxHU \"&#x02569;\">\n" +
            "<!ENTITY boxHu \"&#x02567;\">\n" +
            "<!ENTITY boxhU \"&#x02568;\">\n" +
            "<!ENTITY boxhu \"&#x02534;\">\n" +
            "<!ENTITY boxUL \"&#x0255D;\">\n" +
            "<!ENTITY boxUl \"&#x0255C;\">\n" +
            "<!ENTITY boxuL \"&#x0255B;\">\n" +
            "<!ENTITY boxul \"&#x02518;\">\n" +
            "<!ENTITY boxUR \"&#x0255A;\">\n" +
            "<!ENTITY boxUr \"&#x02559;\">\n" +
            "<!ENTITY boxuR \"&#x02558;\">\n" +
            "<!ENTITY boxur \"&#x02514;\">\n" +
            "<!ENTITY boxV \"&#x02551;\">\n" +
            "<!ENTITY boxv \"&#x02502;\">\n" +
            "<!ENTITY boxVH \"&#x0256C;\">\n" +
            "<!ENTITY boxVh \"&#x0256B;\">\n" +
            "<!ENTITY boxvH \"&#x0256A;\">\n" +
            "<!ENTITY boxvh \"&#x0253C;\">\n" +
            "<!ENTITY boxVL \"&#x02563;\">\n" +
            "<!ENTITY boxVl \"&#x02562;\">\n" +
            "<!ENTITY boxvL \"&#x02561;\">\n" +
            "<!ENTITY boxvl \"&#x02524;\">\n" +
            "<!ENTITY boxVR \"&#x02560;\">\n" +
            "<!ENTITY boxVr \"&#x0255F;\">\n" +
            "<!ENTITY boxvR \"&#x0255E;\">\n" +
            "<!ENTITY boxvr \"&#x0251C;\">\n" +
            "<!ENTITY Acy \"&#x00410;\">\n" +
            "<!ENTITY acy \"&#x00430;\">\n" +
            "<!ENTITY Bcy \"&#x00411;\">\n" +
            "<!ENTITY bcy \"&#x00431;\">\n" +
            "<!ENTITY CHcy \"&#x00427;\">\n" +
            "<!ENTITY chcy \"&#x00447;\">\n" +
            "<!ENTITY Dcy \"&#x00414;\">\n" +
            "<!ENTITY dcy \"&#x00434;\">\n" +
            "<!ENTITY Ecy \"&#x0042D;\">\n" +
            "<!ENTITY ecy \"&#x0044D;\">\n" +
            "<!ENTITY Fcy \"&#x00424;\">\n" +
            "<!ENTITY fcy \"&#x00444;\">\n" +
            "<!ENTITY Gcy \"&#x00413;\">\n" +
            "<!ENTITY gcy \"&#x00433;\">\n" +
            "<!ENTITY HARDcy \"&#x0042A;\">\n" +
            "<!ENTITY hardcy \"&#x0044A;\">\n" +
            "<!ENTITY Icy \"&#x00418;\">\n" +
            "<!ENTITY icy \"&#x00438;\">\n" +
            "<!ENTITY IEcy \"&#x00415;\">\n" +
            "<!ENTITY iecy \"&#x00435;\">\n" +
            "<!ENTITY IOcy \"&#x00401;\">\n" +
            "<!ENTITY iocy \"&#x00451;\">\n" +
            "<!ENTITY Jcy \"&#x00419;\">\n" +
            "<!ENTITY jcy \"&#x00439;\">\n" +
            "<!ENTITY Kcy \"&#x0041A;\">\n" +
            "<!ENTITY kcy \"&#x0043A;\">\n" +
            "<!ENTITY KHcy \"&#x00425;\">\n" +
            "<!ENTITY khcy \"&#x00445;\">\n" +
            "<!ENTITY Lcy \"&#x0041B;\">\n" +
            "<!ENTITY lcy \"&#x0043B;\">\n" +
            "<!ENTITY Mcy \"&#x0041C;\">\n" +
            "<!ENTITY mcy \"&#x0043C;\">\n" +
            "<!ENTITY Ncy \"&#x0041D;\">\n" +
            "<!ENTITY ncy \"&#x0043D;\">\n" +
            "<!ENTITY numero \"&#x02116;\">\n" +
            "<!ENTITY Ocy \"&#x0041E;\">\n" +
            "<!ENTITY ocy \"&#x0043E;\">\n" +
            "<!ENTITY Pcy \"&#x0041F;\">\n" +
            "<!ENTITY pcy \"&#x0043F;\">\n" +
            "<!ENTITY Rcy \"&#x00420;\">\n" +
            "<!ENTITY rcy \"&#x00440;\">\n" +
            "<!ENTITY Scy \"&#x00421;\">\n" +
            "<!ENTITY scy \"&#x00441;\">\n" +
            "<!ENTITY SHCHcy \"&#x00429;\">\n" +
            "<!ENTITY shchcy \"&#x00449;\">\n" +
            "<!ENTITY SHcy \"&#x00428;\">\n" +
            "<!ENTITY shcy \"&#x00448;\">\n" +
            "<!ENTITY SOFTcy \"&#x0042C;\">\n" +
            "<!ENTITY softcy \"&#x0044C;\">\n" +
            "<!ENTITY Tcy \"&#x00422;\">\n" +
            "<!ENTITY tcy \"&#x00442;\">\n" +
            "<!ENTITY TScy \"&#x00426;\">\n" +
            "<!ENTITY tscy \"&#x00446;\">\n" +
            "<!ENTITY Ucy \"&#x00423;\">\n" +
            "<!ENTITY ucy \"&#x00443;\">\n" +
            "<!ENTITY Vcy \"&#x00412;\">\n" +
            "<!ENTITY vcy \"&#x00432;\">\n" +
            "<!ENTITY YAcy \"&#x0042F;\">\n" +
            "<!ENTITY yacy \"&#x0044F;\">\n" +
            "<!ENTITY Ycy \"&#x0042B;\">\n" +
            "<!ENTITY ycy \"&#x0044B;\">\n" +
            "<!ENTITY YUcy \"&#x0042E;\">\n" +
            "<!ENTITY yucy \"&#x0044E;\">\n" +
            "<!ENTITY Zcy \"&#x00417;\">\n" +
            "<!ENTITY zcy \"&#x00437;\">\n" +
            "<!ENTITY ZHcy \"&#x00416;\">\n" +
            "<!ENTITY zhcy \"&#x00436;\">\n" +
            "<!ENTITY DJcy \"&#x00402;\">\n" +
            "<!ENTITY djcy \"&#x00452;\">\n" +
            "<!ENTITY DScy \"&#x00405;\">\n" +
            "<!ENTITY dscy \"&#x00455;\">\n" +
            "<!ENTITY DZcy \"&#x0040F;\">\n" +
            "<!ENTITY dzcy \"&#x0045F;\">\n" +
            "<!ENTITY GJcy \"&#x00403;\">\n" +
            "<!ENTITY gjcy \"&#x00453;\">\n" +
            "<!ENTITY Iukcy \"&#x00406;\">\n" +
            "<!ENTITY iukcy \"&#x00456;\">\n" +
            "<!ENTITY Jsercy \"&#x00408;\">\n" +
            "<!ENTITY jsercy \"&#x00458;\">\n" +
            "<!ENTITY Jukcy \"&#x00404;\">\n" +
            "<!ENTITY jukcy \"&#x00454;\">\n" +
            "<!ENTITY KJcy \"&#x0040C;\">\n" +
            "<!ENTITY kjcy \"&#x0045C;\">\n" +
            "<!ENTITY LJcy \"&#x00409;\">\n" +
            "<!ENTITY ljcy \"&#x00459;\">\n" +
            "<!ENTITY NJcy \"&#x0040A;\">\n" +
            "<!ENTITY njcy \"&#x0045A;\">\n" +
            "<!ENTITY TSHcy \"&#x0040B;\">\n" +
            "<!ENTITY tshcy \"&#x0045B;\">\n" +
            "<!ENTITY Ubrcy \"&#x0040E;\">\n" +
            "<!ENTITY ubrcy \"&#x0045E;\">\n" +
            "<!ENTITY YIcy \"&#x00407;\">\n" +
            "<!ENTITY yicy \"&#x00457;\">\n" +
            "<!ENTITY acute \"&#x000B4;\">\n" +
            "<!ENTITY breve \"&#x002D8;\">\n" +
            "<!ENTITY caron \"&#x002C7;\">\n" +
            "<!ENTITY cedil \"&#x000B8;\">\n" +
            "<!ENTITY circ \"&#x002C6;\">\n" +
            "<!ENTITY dblac \"&#x002DD;\">\n" +
            "<!ENTITY die \"&#x000A8;\">\n" +
            "<!ENTITY dot \"&#x002D9;\">\n" +
            "<!ENTITY grave \"&#x00060;\">\n" +
            "<!ENTITY macr \"&#x000AF;\">\n" +
            "<!ENTITY ogon \"&#x002DB;\">\n" +
            "<!ENTITY ring \"&#x002DA;\">\n" +
            "<!ENTITY tilde \"&#x002DC;\">\n" +
            "<!ENTITY uml \"&#x000A8;\">\n" +
            "<!ENTITY Agr \"&#x00391;\">\n" +
            "<!ENTITY agr \"&#x003B1;\">\n" +
            "<!ENTITY Bgr \"&#x00392;\">\n" +
            "<!ENTITY bgr \"&#x003B2;\">\n" +
            "<!ENTITY Dgr \"&#x00394;\">\n" +
            "<!ENTITY dgr \"&#x003B4;\">\n" +
            "<!ENTITY EEgr \"&#x00397;\">\n" +
            "<!ENTITY eegr \"&#x003B7;\">\n" +
            "<!ENTITY Egr \"&#x00395;\">\n" +
            "<!ENTITY egr \"&#x003B5;\">\n" +
            "<!ENTITY Ggr \"&#x00393;\">\n" +
            "<!ENTITY ggr \"&#x003B3;\">\n" +
            "<!ENTITY Igr \"&#x00399;\">\n" +
            "<!ENTITY igr \"&#x003B9;\">\n" +
            "<!ENTITY Kgr \"&#x0039A;\">\n" +
            "<!ENTITY kgr \"&#x003BA;\">\n" +
            "<!ENTITY KHgr \"&#x003A7;\">\n" +
            "<!ENTITY khgr \"&#x003C7;\">\n" +
            "<!ENTITY Lgr \"&#x0039B;\">\n" +
            "<!ENTITY lgr \"&#x003BB;\">\n" +
            "<!ENTITY Mgr \"&#x0039C;\">\n" +
            "<!ENTITY mgr \"&#x003BC;\">\n" +
            "<!ENTITY Ngr \"&#x0039D;\">\n" +
            "<!ENTITY ngr \"&#x003BD;\">\n" +
            "<!ENTITY Ogr \"&#x0039F;\">\n" +
            "<!ENTITY ogr \"&#x003BF;\">\n" +
            "<!ENTITY OHgr \"&#x003A9;\">\n" +
            "<!ENTITY ohgr \"&#x003C9;\">\n" +
            "<!ENTITY Pgr \"&#x003A0;\">\n" +
            "<!ENTITY pgr \"&#x003C0;\">\n" +
            "<!ENTITY PHgr \"&#x003A6;\">\n" +
            "<!ENTITY phgr \"&#x003C6;\">\n" +
            "<!ENTITY PSgr \"&#x003A8;\">\n" +
            "<!ENTITY psgr \"&#x003C8;\">\n" +
            "<!ENTITY Rgr \"&#x003A1;\">\n" +
            "<!ENTITY rgr \"&#x003C1;\">\n" +
            "<!ENTITY sfgr \"&#x003C2;\">\n" +
            "<!ENTITY Sgr \"&#x003A3;\">\n" +
            "<!ENTITY sgr \"&#x003C3;\">\n" +
            "<!ENTITY Tgr \"&#x003A4;\">\n" +
            "<!ENTITY tgr \"&#x003C4;\">\n" +
            "<!ENTITY THgr \"&#x00398;\">\n" +
            "<!ENTITY thgr \"&#x003B8;\">\n" +
            "<!ENTITY Ugr \"&#x003A5;\">\n" +
            "<!ENTITY ugr \"&#x003C5;\">\n" +
            "<!ENTITY Xgr \"&#x0039E;\">\n" +
            "<!ENTITY xgr \"&#x003BE;\">\n" +
            "<!ENTITY Zgr \"&#x00396;\">\n" +
            "<!ENTITY zgr \"&#x003B6;\">\n" +
            "<!ENTITY Aacgr \"&#x00386;\">\n" +
            "<!ENTITY aacgr \"&#x003AC;\">\n" +
            "<!ENTITY Eacgr \"&#x00388;\">\n" +
            "<!ENTITY eacgr \"&#x003AD;\">\n" +
            "<!ENTITY EEacgr \"&#x00389;\">\n" +
            "<!ENTITY eeacgr \"&#x003AE;\">\n" +
            "<!ENTITY Iacgr \"&#x0038A;\">\n" +
            "<!ENTITY iacgr \"&#x003AF;\">\n" +
            "<!ENTITY idiagr \"&#x00390;\">\n" +
            "<!ENTITY Idigr \"&#x003AA;\">\n" +
            "<!ENTITY idigr \"&#x003CA;\">\n" +
            "<!ENTITY Oacgr \"&#x0038C;\">\n" +
            "<!ENTITY oacgr \"&#x003CC;\">\n" +
            "<!ENTITY OHacgr \"&#x0038F;\">\n" +
            "<!ENTITY ohacgr \"&#x003CE;\">\n" +
            "<!ENTITY Uacgr \"&#x0038E;\">\n" +
            "<!ENTITY uacgr \"&#x003CD;\">\n" +
            "<!ENTITY udiagr \"&#x003B0;\">\n" +
            "<!ENTITY Udigr \"&#x003AB;\">\n" +
            "<!ENTITY udigr \"&#x003CB;\">\n" +
            "<!ENTITY alpha \"&#x003B1;\">\n" +
            "<!ENTITY beta \"&#x003B2;\">\n" +
            "<!ENTITY chi \"&#x003C7;\">\n" +
            "<!ENTITY Delta \"&#x00394;\">\n" +
            "<!ENTITY delta \"&#x003B4;\">\n" +
            "<!ENTITY epsi \"&#x003F5;\">\n" +
            "<!ENTITY epsis \"&#x003F5;\">\n" +
            "<!ENTITY epsiv \"&#x003B5;\">\n" +
            "<!ENTITY eta \"&#x003B7;\">\n" +
            "<!ENTITY Gamma \"&#x00393;\">\n" +
            "<!ENTITY gamma \"&#x003B3;\">\n" +
            "<!ENTITY gammad \"&#x003DD;\">\n" +
            "<!ENTITY iota \"&#x003B9;\">\n" +
            "<!ENTITY kappa \"&#x003BA;\">\n" +
            "<!ENTITY kappav \"&#x003F0;\">\n" +
            "<!ENTITY Lambda \"&#x0039B;\">\n" +
            "<!ENTITY lambda \"&#x003BB;\">\n" +
            "<!ENTITY mu \"&#x003BC;\">\n" +
            "<!ENTITY nu \"&#x003BD;\">\n" +
            "<!ENTITY Omega \"&#x003A9;\">\n" +
            "<!ENTITY omega \"&#x003C9;\">\n" +
            "<!ENTITY Phi \"&#x003A6;\">\n" +
            "<!ENTITY phis \"&#x003D5;\">\n" +
            "<!ENTITY phiv \"&#x003C6;\">\n" +
            "<!ENTITY Pi \"&#x003A0;\">\n" +
            "<!ENTITY pi \"&#x003C0;\">\n" +
            "<!ENTITY piv \"&#x003D6;\">\n" +
            "<!ENTITY Psi \"&#x003A8;\">\n" +
            "<!ENTITY psi \"&#x003C8;\">\n" +
            "<!ENTITY rho \"&#x003C1;\">\n" +
            "<!ENTITY rhov \"&#x003F1;\">\n" +
            "<!ENTITY Sigma \"&#x003A3;\">\n" +
            "<!ENTITY sigma \"&#x003C3;\">\n" +
            "<!ENTITY sigmav \"&#x003C2;\">\n" +
            "<!ENTITY tau \"&#x003C4;\">\n" +
            "<!ENTITY Theta \"&#x00398;\">\n" +
            "<!ENTITY thetas \"&#x003B8;\">\n" +
            "<!ENTITY thetav \"&#x003D1;\">\n" +
            "<!ENTITY Upsi \"&#x003D2;\">\n" +
            "<!ENTITY upsi \"&#x003C5;\">\n" +
            "<!ENTITY Xi \"&#x0039E;\">\n" +
            "<!ENTITY xi \"&#x003BE;\">\n" +
            "<!ENTITY zeta \"&#x003B6;\">\n" +
            "<!ENTITY b.alpha \"&#x1D6C2;\">\n" +
            "<!ENTITY b.beta \"&#x1D6C3;\">\n" +
            "<!ENTITY b.chi \"&#x1D6D8;\">\n" +
            "<!ENTITY b.Delta \"&#x1D6AB;\">\n" +
            "<!ENTITY b.delta \"&#x1D6C5;\">\n" +
            "<!ENTITY b.epsi \"&#x1D6C6;\">\n" +
            "<!ENTITY b.epsiv \"&#x1D6DC;\">\n" +
            "<!ENTITY b.eta \"&#x1D6C8;\">\n" +
            "<!ENTITY b.Gamma \"&#x1D6AA;\">\n" +
            "<!ENTITY b.gamma \"&#x1D6C4;\">\n" +
            "<!ENTITY b.Gammad \"&#x003DC;\">\n" +
            "<!ENTITY b.gammad \"&#x003DD;\">\n" +
            "<!ENTITY b.iota \"&#x1D6CA;\">\n" +
            "<!ENTITY b.kappa \"&#x1D6CB;\">\n" +
            "<!ENTITY b.kappav \"&#x1D6DE;\">\n" +
            "<!ENTITY b.Lambda \"&#x1D6B2;\">\n" +
            "<!ENTITY b.lambda \"&#x1D6CC;\">\n" +
            "<!ENTITY b.mu \"&#x1D6CD;\">\n" +
            "<!ENTITY b.nu \"&#x1D6CE;\">\n" +
            "<!ENTITY b.Omega \"&#x1D6C0;\">\n" +
            "<!ENTITY b.omega \"&#x1D6DA;\">\n" +
            "<!ENTITY b.Phi \"&#x1D6BD;\">\n" +
            "<!ENTITY b.phi \"&#x1D6D7;\">\n" +
            "<!ENTITY b.phiv \"&#x1D6DF;\">\n" +
            "<!ENTITY b.Pi \"&#x1D6B7;\">\n" +
            "<!ENTITY b.pi \"&#x1D6D1;\">\n" +
            "<!ENTITY b.piv \"&#x1D6E1;\">\n" +
            "<!ENTITY b.Psi \"&#x1D6BF;\">\n" +
            "<!ENTITY b.psi \"&#x1D6D9;\">\n" +
            "<!ENTITY b.rho \"&#x1D6D2;\">\n" +
            "<!ENTITY b.rhov \"&#x1D6E0;\">\n" +
            "<!ENTITY b.Sigma \"&#x1D6BA;\">\n" +
            "<!ENTITY b.sigma \"&#x1D6D4;\">\n" +
            "<!ENTITY b.sigmav \"&#x1D6D3;\">\n" +
            "<!ENTITY b.tau \"&#x1D6D5;\">\n" +
            "<!ENTITY b.Theta \"&#x1D6AF;\">\n" +
            "<!ENTITY b.thetas \"&#x1D6C9;\">\n" +
            "<!ENTITY b.thetav \"&#x1D6DD;\">\n" +
            "<!ENTITY b.Upsi \"&#x1D6BC;\">\n" +
            "<!ENTITY b.upsi \"&#x1D6D6;\">\n" +
            "<!ENTITY b.Xi \"&#x1D6B5;\">\n" +
            "<!ENTITY b.xi \"&#x1D6CF;\">\n" +
            "<!ENTITY b.zeta \"&#x1D6C7;\">\n" +
            "<!ENTITY Aacute \"&#x000C1;\">\n" +
            "<!ENTITY aacute \"&#x000E1;\">\n" +
            "<!ENTITY Acirc \"&#x000C2;\">\n" +
            "<!ENTITY acirc \"&#x000E2;\">\n" +
            "<!ENTITY AElig \"&#x000C6;\">\n" +
            "<!ENTITY aelig \"&#x000E6;\">\n" +
            "<!ENTITY Agrave \"&#x000C0;\">\n" +
            "<!ENTITY agrave \"&#x000E0;\">\n" +
            "<!ENTITY Aring \"&#x000C5;\">\n" +
            "<!ENTITY aring \"&#x000E5;\">\n" +
            "<!ENTITY Atilde \"&#x000C3;\">\n" +
            "<!ENTITY atilde \"&#x000E3;\">\n" +
            "<!ENTITY Auml \"&#x000C4;\">\n" +
            "<!ENTITY auml \"&#x000E4;\">\n" +
            "<!ENTITY Ccedil \"&#x000C7;\">\n" +
            "<!ENTITY ccedil \"&#x000E7;\">\n" +
            "<!ENTITY Eacute \"&#x000C9;\">\n" +
            "<!ENTITY eacute \"&#x000E9;\">\n" +
            "<!ENTITY Ecirc \"&#x000CA;\">\n" +
            "<!ENTITY ecirc \"&#x000EA;\">\n" +
            "<!ENTITY Egrave \"&#x000C8;\">\n" +
            "<!ENTITY egrave \"&#x000E8;\">\n" +
            "<!ENTITY ETH \"&#x000D0;\">\n" +
            "<!ENTITY eth \"&#x000F0;\">\n" +
            "<!ENTITY Euml \"&#x000CB;\">\n" +
            "<!ENTITY euml \"&#x000EB;\">\n" +
            "<!ENTITY Iacute \"&#x000CD;\">\n" +
            "<!ENTITY iacute \"&#x000ED;\">\n" +
            "<!ENTITY Icirc \"&#x000CE;\">\n" +
            "<!ENTITY icirc \"&#x000EE;\">\n" +
            "<!ENTITY Igrave \"&#x000CC;\">\n" +
            "<!ENTITY igrave \"&#x000EC;\">\n" +
            "<!ENTITY Iuml \"&#x000CF;\">\n" +
            "<!ENTITY iuml \"&#x000EF;\">\n" +
            "<!ENTITY Ntilde \"&#x000D1;\">\n" +
            "<!ENTITY ntilde \"&#x000F1;\">\n" +
            "<!ENTITY Oacute \"&#x000D3;\">\n" +
            "<!ENTITY oacute \"&#x000F3;\">\n" +
            "<!ENTITY Ocirc \"&#x000D4;\">\n" +
            "<!ENTITY ocirc \"&#x000F4;\">\n" +
            "<!ENTITY Ograve \"&#x000D2;\">\n" +
            "<!ENTITY ograve \"&#x000F2;\">\n" +
            "<!ENTITY Oslash \"&#x000D8;\">\n" +
            "<!ENTITY oslash \"&#x000F8;\">\n" +
            "<!ENTITY Otilde \"&#x000D5;\">\n" +
            "<!ENTITY otilde \"&#x000F5;\">\n" +
            "<!ENTITY Ouml \"&#x000D6;\">\n" +
            "<!ENTITY ouml \"&#x000F6;\">\n" +
            "<!ENTITY szlig \"&#x000DF;\">\n" +
            "<!ENTITY THORN \"&#x000DE;\">\n" +
            "<!ENTITY thorn \"&#x000FE;\">\n" +
            "<!ENTITY Uacute \"&#x000DA;\">\n" +
            "<!ENTITY uacute \"&#x000FA;\">\n" +
            "<!ENTITY Ucirc \"&#x000DB;\">\n" +
            "<!ENTITY ucirc \"&#x000FB;\">\n" +
            "<!ENTITY Ugrave \"&#x000D9;\">\n" +
            "<!ENTITY ugrave \"&#x000F9;\">\n" +
            "<!ENTITY Uuml \"&#x000DC;\">\n" +
            "<!ENTITY uuml \"&#x000FC;\">\n" +
            "<!ENTITY Yacute \"&#x000DD;\">\n" +
            "<!ENTITY yacute \"&#x000FD;\">\n" +
            "<!ENTITY yuml \"&#x000FF;\">\n" +
            "<!ENTITY Abreve \"&#x00102;\">\n" +
            "<!ENTITY abreve \"&#x00103;\">\n" +
            "<!ENTITY Amacr \"&#x00100;\">\n" +
            "<!ENTITY amacr \"&#x00101;\">\n" +
            "<!ENTITY Aogon \"&#x00104;\">\n" +
            "<!ENTITY aogon \"&#x00105;\">\n" +
            "<!ENTITY Cacute \"&#x00106;\">\n" +
            "<!ENTITY cacute \"&#x00107;\">\n" +
            "<!ENTITY Ccaron \"&#x0010C;\">\n" +
            "<!ENTITY ccaron \"&#x0010D;\">\n" +
            "<!ENTITY Ccirc \"&#x00108;\">\n" +
            "<!ENTITY ccirc \"&#x00109;\">\n" +
            "<!ENTITY Cdot \"&#x0010A;\">\n" +
            "<!ENTITY cdot \"&#x0010B;\">\n" +
            "<!ENTITY Dcaron \"&#x0010E;\">\n" +
            "<!ENTITY dcaron \"&#x0010F;\">\n" +
            "<!ENTITY Dstrok \"&#x00110;\">\n" +
            "<!ENTITY dstrok \"&#x00111;\">\n" +
            "<!ENTITY Ecaron \"&#x0011A;\">\n" +
            "<!ENTITY ecaron \"&#x0011B;\">\n" +
            "<!ENTITY Edot \"&#x00116;\">\n" +
            "<!ENTITY edot \"&#x00117;\">\n" +
            "<!ENTITY Emacr \"&#x00112;\">\n" +
            "<!ENTITY emacr \"&#x00113;\">\n" +
            "<!ENTITY ENG \"&#x0014A;\">\n" +
            "<!ENTITY eng \"&#x0014B;\">\n" +
            "<!ENTITY Eogon \"&#x00118;\">\n" +
            "<!ENTITY eogon \"&#x00119;\">\n" +
            "<!ENTITY gacute \"&#x001F5;\">\n" +
            "<!ENTITY Gbreve \"&#x0011E;\">\n" +
            "<!ENTITY gbreve \"&#x0011F;\">\n" +
            "<!ENTITY Gcedil \"&#x00122;\">\n" +
            "<!ENTITY Gcirc \"&#x0011C;\">\n" +
            "<!ENTITY gcirc \"&#x0011D;\">\n" +
            "<!ENTITY Gdot \"&#x00120;\">\n" +
            "<!ENTITY gdot \"&#x00121;\">\n" +
            "<!ENTITY Hcirc \"&#x00124;\">\n" +
            "<!ENTITY hcirc \"&#x00125;\">\n" +
            "<!ENTITY Hstrok \"&#x00126;\">\n" +
            "<!ENTITY hstrok \"&#x00127;\">\n" +
            "<!ENTITY Idot \"&#x00130;\">\n" +
            "<!ENTITY IJlig \"&#x00132;\">\n" +
            "<!ENTITY ijlig \"&#x00133;\">\n" +
            "<!ENTITY Imacr \"&#x0012A;\">\n" +
            "<!ENTITY imacr \"&#x0012B;\">\n" +
            "<!ENTITY inodot \"&#x00131;\">\n" +
            "<!ENTITY Iogon \"&#x0012E;\">\n" +
            "<!ENTITY iogon \"&#x0012F;\">\n" +
            "<!ENTITY Itilde \"&#x00128;\">\n" +
            "<!ENTITY itilde \"&#x00129;\">\n" +
            "<!ENTITY Jcirc \"&#x00134;\">\n" +
            "<!ENTITY jcirc \"&#x00135;\">\n" +
            "<!ENTITY Kcedil \"&#x00136;\">\n" +
            "<!ENTITY kcedil \"&#x00137;\">\n" +
            "<!ENTITY kgreen \"&#x00138;\">\n" +
            "<!ENTITY Lacute \"&#x00139;\">\n" +
            "<!ENTITY lacute \"&#x0013A;\">\n" +
            "<!ENTITY Lcaron \"&#x0013D;\">\n" +
            "<!ENTITY lcaron \"&#x0013E;\">\n" +
            "<!ENTITY Lcedil \"&#x0013B;\">\n" +
            "<!ENTITY lcedil \"&#x0013C;\">\n" +
            "<!ENTITY Lmidot \"&#x0013F;\">\n" +
            "<!ENTITY lmidot \"&#x00140;\">\n" +
            "<!ENTITY Lstrok \"&#x00141;\">\n" +
            "<!ENTITY lstrok \"&#x00142;\">\n" +
            "<!ENTITY Nacute \"&#x00143;\">\n" +
            "<!ENTITY nacute \"&#x00144;\">\n" +
            "<!ENTITY napos \"&#x00149;\">\n" +
            "<!ENTITY Ncaron \"&#x00147;\">\n" +
            "<!ENTITY ncaron \"&#x00148;\">\n" +
            "<!ENTITY Ncedil \"&#x00145;\">\n" +
            "<!ENTITY ncedil \"&#x00146;\">\n" +
            "<!ENTITY Odblac \"&#x00150;\">\n" +
            "<!ENTITY odblac \"&#x00151;\">\n" +
            "<!ENTITY OElig \"&#x00152;\">\n" +
            "<!ENTITY oelig \"&#x00153;\">\n" +
            "<!ENTITY Omacr \"&#x0014C;\">\n" +
            "<!ENTITY omacr \"&#x0014D;\">\n" +
            "<!ENTITY Racute \"&#x00154;\">\n" +
            "<!ENTITY racute \"&#x00155;\">\n" +
            "<!ENTITY Rcaron \"&#x00158;\">\n" +
            "<!ENTITY rcaron \"&#x00159;\">\n" +
            "<!ENTITY Rcedil \"&#x00156;\">\n" +
            "<!ENTITY rcedil \"&#x00157;\">\n" +
            "<!ENTITY Sacute \"&#x0015A;\">\n" +
            "<!ENTITY sacute \"&#x0015B;\">\n" +
            "<!ENTITY Scaron \"&#x00160;\">\n" +
            "<!ENTITY scaron \"&#x00161;\">\n" +
            "<!ENTITY Scedil \"&#x0015E;\">\n" +
            "<!ENTITY scedil \"&#x0015F;\">\n" +
            "<!ENTITY Scirc \"&#x0015C;\">\n" +
            "<!ENTITY scirc \"&#x0015D;\">\n" +
            "<!ENTITY Tcaron \"&#x00164;\">\n" +
            "<!ENTITY tcaron \"&#x00165;\">\n" +
            "<!ENTITY Tcedil \"&#x00162;\">\n" +
            "<!ENTITY tcedil \"&#x00163;\">\n" +
            "<!ENTITY Tstrok \"&#x00166;\">\n" +
            "<!ENTITY tstrok \"&#x00167;\">\n" +
            "<!ENTITY Ubreve \"&#x0016C;\">\n" +
            "<!ENTITY ubreve \"&#x0016D;\">\n" +
            "<!ENTITY Udblac \"&#x00170;\">\n" +
            "<!ENTITY udblac \"&#x00171;\">\n" +
            "<!ENTITY Umacr \"&#x0016A;\">\n" +
            "<!ENTITY umacr \"&#x0016B;\">\n" +
            "<!ENTITY Uogon \"&#x00172;\">\n" +
            "<!ENTITY uogon \"&#x00173;\">\n" +
            "<!ENTITY Uring \"&#x0016E;\">\n" +
            "<!ENTITY uring \"&#x0016F;\">\n" +
            "<!ENTITY Utilde \"&#x00168;\">\n" +
            "<!ENTITY utilde \"&#x00169;\">\n" +
            "<!ENTITY Wcirc \"&#x00174;\">\n" +
            "<!ENTITY wcirc \"&#x00175;\">\n" +
            "<!ENTITY Ycirc \"&#x00176;\">\n" +
            "<!ENTITY ycirc \"&#x00177;\">\n" +
            "<!ENTITY Yuml \"&#x00178;\">\n" +
            "<!ENTITY Zacute \"&#x00179;\">\n" +
            "<!ENTITY zacute \"&#x0017A;\">\n" +
            "<!ENTITY Zcaron \"&#x0017D;\">\n" +
            "<!ENTITY zcaron \"&#x0017E;\">\n" +
            "<!ENTITY Zdot \"&#x0017B;\">\n" +
            "<!ENTITY zdot \"&#x0017C;\">\n" +
            "<!ENTITY amp \"&#38;#38;\">\n" +
            "<!ENTITY apos \"&#x00027;\">\n" +
            "<!ENTITY ast \"&#x0002A;\">\n" +
            "<!ENTITY brvbar \"&#x000A6;\">\n" +
            "<!ENTITY bsol \"&#x0005C;\">\n" +
            "<!ENTITY cent \"&#x000A2;\">\n" +
            "<!ENTITY colon \"&#x0003A;\">\n" +
            "<!ENTITY comma \"&#x0002C;\">\n" +
            "<!ENTITY commat \"&#x00040;\">\n" +
            "<!ENTITY copy \"&#x000A9;\">\n" +
            "<!ENTITY curren \"&#x000A4;\">\n" +
            "<!ENTITY darr \"&#x02193;\">\n" +
            "<!ENTITY deg \"&#x000B0;\">\n" +
            "<!ENTITY divide \"&#x000F7;\">\n" +
            "<!ENTITY dollar \"&#x00024;\">\n" +
            "<!ENTITY equals \"&#x0003D;\">\n" +
            "<!ENTITY excl \"&#x00021;\">\n" +
            "<!ENTITY frac12 \"&#x000BD;\">\n" +
            "<!ENTITY frac14 \"&#x000BC;\">\n" +
            "<!ENTITY frac18 \"&#x0215B;\">\n" +
            "<!ENTITY frac34 \"&#x000BE;\">\n" +
            "<!ENTITY frac38 \"&#x0215C;\">\n" +
            "<!ENTITY frac58 \"&#x0215D;\">\n" +
            "<!ENTITY frac78 \"&#x0215E;\">\n" +
            "<!ENTITY gt \"&#x0003E;\">\n" +
            "<!ENTITY half \"&#x000BD;\">\n" +
            "<!ENTITY horbar \"&#x02015;\">\n" +
            "<!ENTITY hyphen \"&#x02010;\">\n" +
            "<!ENTITY iexcl \"&#x000A1;\">\n" +
            "<!ENTITY iquest \"&#x000BF;\">\n" +
            "<!ENTITY laquo \"&#x000AB;\">\n" +
            "<!ENTITY larr \"&#x02190;\">\n" +
            "<!ENTITY lcub \"&#x0007B;\">\n" +
            "<!ENTITY ldquo \"&#x0201C;\">\n" +
            "<!ENTITY lowbar \"&#x0005F;\">\n" +
            "<!ENTITY lpar \"&#x00028;\">\n" +
            "<!ENTITY lsqb \"&#x0005B;\">\n" +
            "<!ENTITY lsquo \"&#x02018;\">\n" +
            "<!ENTITY lt \"&#38;#60;\">\n" +
            "<!ENTITY micro \"&#x000B5;\">\n" +
            "<!ENTITY middot \"&#x000B7;\">\n" +
            "<!ENTITY nbsp \"&#x000A0;\">\n" +
            "<!ENTITY not \"&#x000AC;\">\n" +
            "<!ENTITY num \"&#x00023;\">\n" +
            "<!ENTITY ohm \"&#x02126;\">\n" +
            "<!ENTITY ordf \"&#x000AA;\">\n" +
            "<!ENTITY ordm \"&#x000BA;\">\n" +
            "<!ENTITY para \"&#x000B6;\">\n" +
            "<!ENTITY percnt \"&#x00025;\">\n" +
            "<!ENTITY period \"&#x0002E;\">\n" +
            "<!ENTITY plus \"&#x0002B;\">\n" +
            "<!ENTITY plusmn \"&#x000B1;\">\n" +
            "<!ENTITY pound \"&#x000A3;\">\n" +
            "<!ENTITY quest \"&#x0003F;\">\n" +
            "<!ENTITY quot \"&#x00022;\">\n" +
            "<!ENTITY raquo \"&#x000BB;\">\n" +
            "<!ENTITY rarr \"&#x02192;\">\n" +
            "<!ENTITY rcub \"&#x0007D;\">\n" +
            "<!ENTITY rdquo \"&#x0201D;\">\n" +
            "<!ENTITY reg \"&#x000AE;\">\n" +
            "<!ENTITY rpar \"&#x00029;\">\n" +
            "<!ENTITY rsqb \"&#x0005D;\">\n" +
            "<!ENTITY rsquo \"&#x02019;\">\n" +
            "<!ENTITY sect \"&#x000A7;\">\n" +
            "<!ENTITY semi \"&#x0003B;\">\n" +
            "<!ENTITY shy \"&#x000AD;\">\n" +
            "<!ENTITY sol \"&#x0002F;\">\n" +
            "<!ENTITY sung \"&#x0266A;\">\n" +
            "<!ENTITY sup1 \"&#x000B9;\">\n" +
            "<!ENTITY sup2 \"&#x000B2;\">\n" +
            "<!ENTITY sup3 \"&#x000B3;\">\n" +
            "<!ENTITY times \"&#x000D7;\">\n" +
            "<!ENTITY trade \"&#x02122;\">\n" +
            "<!ENTITY uarr \"&#x02191;\">\n" +
            "<!ENTITY verbar \"&#x0007C;\">\n" +
            "<!ENTITY yen \"&#x000A5;\">\n" +
            "<!ENTITY blank \"&#x02423;\">\n" +
            "<!ENTITY blk12 \"&#x02592;\">\n" +
            "<!ENTITY blk14 \"&#x02591;\">\n" +
            "<!ENTITY blk34 \"&#x02593;\">\n" +
            "<!ENTITY block \"&#x02588;\">\n" +
            "<!ENTITY bull \"&#x02022;\">\n" +
            "<!ENTITY caret \"&#x02041;\">\n" +
            "<!ENTITY check \"&#x02713;\">\n" +
            "<!ENTITY cir \"&#x025CB;\">\n" +
            "<!ENTITY clubs \"&#x02663;\">\n" +
            "<!ENTITY copysr \"&#x02117;\">\n" +
            "<!ENTITY cross \"&#x02717;\">\n" +
            "<!ENTITY Dagger \"&#x02021;\">\n" +
            "<!ENTITY dagger \"&#x02020;\">\n" +
            "<!ENTITY dash \"&#x02010;\">\n" +
            "<!ENTITY diams \"&#x02666;\">\n" +
            "<!ENTITY dlcrop \"&#x0230D;\">\n" +
            "<!ENTITY drcrop \"&#x0230C;\">\n" +
            "<!ENTITY dtri \"&#x025BF;\">\n" +
            "<!ENTITY dtrif \"&#x025BE;\">\n" +
            "<!ENTITY emsp \"&#x02003;\">\n" +
            "<!ENTITY emsp13 \"&#x02004;\">\n" +
            "<!ENTITY emsp14 \"&#x02005;\">\n" +
            "<!ENTITY ensp \"&#x02002;\">\n" +
            "<!ENTITY female \"&#x02640;\">\n" +
            "<!ENTITY ffilig \"&#x0FB03;\">\n" +
            "<!ENTITY fflig \"&#x0FB00;\">\n" +
            "<!ENTITY ffllig \"&#x0FB04;\">\n" +
            "<!ENTITY filig \"&#x0FB01;\">\n" +
            "<!ENTITY flat \"&#x0266D;\">\n" +
            "<!ENTITY fllig \"&#x0FB02;\">\n" +
            "<!ENTITY frac13 \"&#x02153;\">\n" +
            "<!ENTITY frac15 \"&#x02155;\">\n" +
            "<!ENTITY frac16 \"&#x02159;\">\n" +
            "<!ENTITY frac23 \"&#x02154;\">\n" +
            "<!ENTITY frac25 \"&#x02156;\">\n" +
            "<!ENTITY frac35 \"&#x02157;\">\n" +
            "<!ENTITY frac45 \"&#x02158;\">\n" +
            "<!ENTITY frac56 \"&#x0215A;\">\n" +
            "<!ENTITY hairsp \"&#x0200A;\">\n" +
            "<!ENTITY hearts \"&#x02665;\">\n" +
            "<!ENTITY hellip \"&#x02026;\">\n" +
            "<!ENTITY hybull \"&#x02043;\">\n" +
            "<!ENTITY incare \"&#x02105;\">\n" +
            "<!ENTITY ldquor \"&#x0201E;\">\n" +
            "<!ENTITY lhblk \"&#x02584;\">\n" +
            "<!ENTITY loz \"&#x025CA;\">\n" +
            "<!ENTITY lozf \"&#x029EB;\">\n" +
            "<!ENTITY lsquor \"&#x0201A;\">\n" +
            "<!ENTITY ltri \"&#x025C3;\">\n" +
            "<!ENTITY ltrif \"&#x025C2;\">\n" +
            "<!ENTITY male \"&#x02642;\">\n" +
            "<!ENTITY malt \"&#x02720;\">\n" +
            "<!ENTITY marker \"&#x025AE;\">\n" +
            "<!ENTITY mdash \"&#x02014;\">\n" +
            "<!ENTITY mldr \"&#x02026;\">\n" +
            "<!ENTITY natur \"&#x0266E;\">\n" +
            "<!ENTITY ndash \"&#x02013;\">\n" +
            "<!ENTITY nldr \"&#x02025;\">\n" +
            "<!ENTITY numsp \"&#x02007;\">\n" +
            "<!ENTITY phone \"&#x0260E;\">\n" +
            "<!ENTITY puncsp \"&#x02008;\">\n" +
            "<!ENTITY rdquor \"&#x0201D;\">\n" +
            "<!ENTITY rect \"&#x025AD;\">\n" +
            "<!ENTITY rsquor \"&#x02019;\">\n" +
            "<!ENTITY rtri \"&#x025B9;\">\n" +
            "<!ENTITY rtrif \"&#x025B8;\">\n" +
            "<!ENTITY rx \"&#x0211E;\">\n" +
            "<!ENTITY sext \"&#x02736;\">\n" +
            "<!ENTITY sharp \"&#x0266F;\">\n" +
            "<!ENTITY spades \"&#x02660;\">\n" +
            "<!ENTITY squ \"&#x025A1;\">\n" +
            "<!ENTITY squf \"&#x025AA;\">\n" +
            "<!ENTITY star \"&#x02606;\">\n" +
            "<!ENTITY starf \"&#x02605;\">\n" +
            "<!ENTITY target \"&#x02316;\">\n" +
            "<!ENTITY telrec \"&#x02315;\">\n" +
            "<!ENTITY thinsp \"&#x02009;\">\n" +
            "<!ENTITY uhblk \"&#x02580;\">\n" +
            "<!ENTITY ulcrop \"&#x0230F;\">\n" +
            "<!ENTITY urcrop \"&#x0230E;\">\n" +
            "<!ENTITY utri \"&#x025B5;\">\n" +
            "<!ENTITY utrif \"&#x025B4;\">\n" +
            "<!ENTITY vellip \"&#x022EE;\">\n" +
            "<!ENTITY aleph \"&#x02135;\">\n" +
            "<!ENTITY and \"&#x02227;\">\n" +
            "<!ENTITY ang90 \"&#x0221F;\">\n" +
            "<!ENTITY angsph \"&#x02222;\">\n" +
            "<!ENTITY angst \"&#x0212B;\">\n" +
            "<!ENTITY ap \"&#x02248;\">\n" +
            "<!ENTITY becaus \"&#x02235;\">\n" +
            "<!ENTITY bernou \"&#x0212C;\">\n" +
            "<!ENTITY bottom \"&#x022A5;\">\n" +
            "<!ENTITY cap \"&#x02229;\">\n" +
            "<!ENTITY compfn \"&#x02218;\">\n" +
            "<!ENTITY cong \"&#x02245;\">\n" +
            "<!ENTITY conint \"&#x0222E;\">\n" +
            "<!ENTITY cup \"&#x0222A;\">\n" +
            "<!ENTITY Dot \"&#x000A8;\">\n" +
            "<!ENTITY DotDot \" &#x020DC;\">\n" +
            "<!ENTITY equiv \"&#x02261;\">\n" +
            "<!ENTITY exist \"&#x02203;\">\n" +
            "<!ENTITY fnof \"&#x00192;\">\n" +
            "<!ENTITY forall \"&#x02200;\">\n" +
            "<!ENTITY ge \"&#x02265;\">\n" +
            "<!ENTITY hamilt \"&#x0210B;\">\n" +
            "<!ENTITY iff \"&#x021D4;\">\n" +
            "<!ENTITY infin \"&#x0221E;\">\n" +
            "<!ENTITY int \"&#x0222B;\">\n" +
            "<!ENTITY isin \"&#x02208;\">\n" +
            "<!ENTITY lagran \"&#x02112;\">\n" +
            "<!ENTITY lang \"&#x02329;\">\n" +
            "<!ENTITY lArr \"&#x021D0;\">\n" +
            "<!ENTITY le \"&#x02264;\">\n" +
            "<!ENTITY lowast \"&#x02217;\">\n" +
            "<!ENTITY minus \"&#x02212;\">\n" +
            "<!ENTITY mnplus \"&#x02213;\">\n" +
            "<!ENTITY nabla \"&#x02207;\">\n" +
            "<!ENTITY ne \"&#x02260;\">\n" +
            "<!ENTITY ni \"&#x0220B;\">\n" +
            "<!ENTITY notin \"&#x02209;\">\n" +
            "<!ENTITY or \"&#x02228;\">\n" +
            "<!ENTITY order \"&#x02134;\">\n" +
            "<!ENTITY par \"&#x02225;\">\n" +
            "<!ENTITY part \"&#x02202;\">\n" +
            "<!ENTITY permil \"&#x02030;\">\n" +
            "<!ENTITY perp \"&#x022A5;\">\n" +
            "<!ENTITY phmmat \"&#x02133;\">\n" +
            "<!ENTITY Prime \"&#x02033;\">\n" +
            "<!ENTITY prime \"&#x02032;\">\n" +
            "<!ENTITY prop \"&#x0221D;\">\n" +
            "<!ENTITY radic \"&#x0221A;\">\n" +
            "<!ENTITY rang \"&#x0232A;\">\n" +
            "<!ENTITY rArr \"&#x021D2;\">\n" +
            "<!ENTITY sim \"&#x0223C;\">\n" +
            "<!ENTITY sime \"&#x02243;\">\n" +
            "<!ENTITY square \"&#x025A1;\">\n" +
            "<!ENTITY sub \"&#x02282;\">\n" +
            "<!ENTITY sube \"&#x02286;\">\n" +
            "<!ENTITY sup \"&#x02283;\">\n" +
            "<!ENTITY supe \"&#x02287;\">\n" +
            "<!ENTITY tdot \" &#x020DB;\">\n" +
            "<!ENTITY there4 \"&#x02234;\">\n" +
            "<!ENTITY tprime \"&#x02034;\">\n" +
            "<!ENTITY Verbar \"&#x02016;\">\n" +
            "<!ENTITY wedgeq \"&#x02259;\">";

    public static boolean allEntitiesAccountedFor(final Node xmlNode, final DocBookVersion format, final List<String> entities) {
        final NodeList nodeList = xmlNode.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); ++i) {
            final Node node = nodeList.item(i);
            if (node.getNodeType() == Node.ENTITY_REFERENCE_NODE) {
                final String nodeName = node.getNodeName();
                /*
                    See if this entity has been supplied in the list
                 */
                if (entities == null || entities.indexOf(nodeName) == -1) {
                    /*
                        See if this entity is a default one
                     */
                    if (format == DocBookVersion.DOCBOOK_50 || format == DocBookVersion.DOCBOOK_45) {
                        if (!DOCBOOK_ENTITIES.containsKey(nodeName)) {
                            return false;
                        }
                    } else {
                        return false;
                    }
                }
            } else {
                if (!allEntitiesAccountedFor(node, format, entities)) {
                    return false;
                }
            }
        }

        return true;
    }

    public static boolean allEntitiesAccountedFor(final Node xmlNode, final DocBookVersion format, final String entities) {
        final NodeList nodeList = xmlNode.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); ++i) {
            final Node node = nodeList.item(i);
            if (node.getNodeType() == Node.ENTITY_REFERENCE_NODE) {
                final String nodeName = node.getNodeName();
                final Pattern entityPattern = Pattern.compile("<!ENTITY\\s+" + nodeName + "\\s+");
                if (entities == null || !entityPattern.matcher(entities).find()) {
                    if (format == DocBookVersion.DOCBOOK_50 || format == DocBookVersion.DOCBOOK_45) {
                        if (!DOCBOOK_ENTITIES.containsKey(nodeName)) {
                            return false;
                        }
                    } else {
                        return false;
                    }
                }
            } else {
                if (!allEntitiesAccountedFor(node, format, entities)) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Finds the first title element in a DocBook XML file.
     *
     * @param xml The docbook xml file to find the title from.
     * @return The first title found in the xml.
     */
    public static String findTitle(final String xml) {
        // Convert the string to a document to make it easier to get the proper title
        Document doc = null;
        try {
            doc = XMLUtilities.convertStringToDocument(xml);
        } catch (Exception ex) {
            LOG.debug("Unable to convert String to a DOM Document", ex);
        }

        return findTitle(doc);
    }

    /**
     * Finds the first title element in a DocBook XML file.
     *
     * @param doc The docbook xml transformed into a DOM Document to find the title from.
     * @return The first title found in the xml.
     */
    public static String findTitle(final Document doc) {
        if (doc == null) return null;

        // loop through the child nodes until the title element is found
        final NodeList childNodes = doc.getDocumentElement().getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node node = childNodes.item(i);

            // check if the node is the title and if its parent is the document root element
            if (node.getNodeName().equals(TOPIC_ROOT_TITLE_NODE_NAME) && node.getParentNode().equals(doc.getDocumentElement())) {
                return XMLUtilities.convertNodeToString(node, false);
            }
        }

        return null;
    }

    /**
     * Escapes a title so that it is alphanumeric or has a fullstop, underscore or hyphen only.
     * It also removes anything from the front of the title that isn't alphanumeric.
     *
     * @param title The title to be escaped
     * @return The escaped title string.
     */
    public static String escapeTitle(final String title) {
        final String escapedTitle = title.replaceAll("^[^" + UNICODE_TITLE_START_CHAR + "]*", "").replaceAll("[^" + UNICODE_WORD + ". -]",
                "");
        if (isNullOrEmpty(escapedTitle)) {
            return "";
        } else {
            // Remove whitespace
            return escapedTitle.replaceAll("\\s+", "_").replaceAll("(^_+)|(_+$)", "").replaceAll("__", "_");
        }
    }

    public static void setSectionTitle(final DocBookVersion docBookVersion, final String titleValue, final Document doc) {
        assert doc != null : "The doc parameter can not be null";
        final Element docElement = doc.getDocumentElement();

        // Check to make sure the document is a section. If it isn't than just return
        if (docElement == null || !docElement.getNodeName().equals(DocBookUtilities.TOPIC_ROOT_NODE_NAME)) return;

        // Attempt to parse the title as XML. If this fails then just set the title as plain text.
        final Element newTitle = doc.createElement(DocBookUtilities.TOPIC_ROOT_TITLE_NODE_NAME);
        try {
            final Document tempDoc = XMLUtilities.convertStringToDocument("<title>" + escapeForXML(titleValue) + "</title>");
            final Node titleEle = doc.importNode(tempDoc.getDocumentElement(), true);

            // Add the child elements to the ulink node
            final NodeList nodes = titleEle.getChildNodes();
            while (nodes.getLength() > 0) {
                newTitle.appendChild(nodes.item(0));
            }
        } catch (Exception e) {
            newTitle.appendChild(doc.createTextNode(titleValue));
        }

        final NodeList titleNodes = docElement.getElementsByTagName(DocBookUtilities.TOPIC_ROOT_TITLE_NODE_NAME);
        // see if we have a title node whose parent is the section
        if (titleNodes.getLength() != 0 && titleNodes.item(0).getParentNode().equals(docElement)) {
            final Node title = titleNodes.item(0);
            title.getParentNode().replaceChild(newTitle, title);
        } else {
            // Find the first node that isn't text or a comment
            Node firstNode = docElement.getFirstChild();
            while (firstNode != null && firstNode.getNodeType() != Node.ELEMENT_NODE) {
                firstNode = firstNode.getNextSibling();
            }

            // DocBook 5.0+ changed where the info node needs to be. In 5.0+ it is after the title, whereas 4.5 has to be before the title.
            if (docBookVersion == DocBookVersion.DOCBOOK_50) {
                if (firstNode != null) {
                    docElement.insertBefore(newTitle, firstNode);
                } else {
                    docElement.appendChild(newTitle);
                }
            } else {
                // Set the section title based on if the first node is a "sectioninfo" node.
                if (firstNode != null && firstNode.getNodeName().equals(DocBookUtilities.TOPIC_ROOT_SECTIONINFO_NODE_NAME)) {
                    final Node nextNode = firstNode.getNextSibling();
                    if (nextNode != null) {
                        docElement.insertBefore(newTitle, nextNode);
                    } else {
                        docElement.appendChild(newTitle);
                    }
                } else if (firstNode != null) {
                    docElement.insertBefore(newTitle, firstNode);
                } else {
                    docElement.appendChild(newTitle);
                }
            }
        }
    }

    public static void setRootElementTitle(final String titleValue, final Document doc) {
        assert doc != null : "The doc parameter can not be null";

        final Element newTitle = doc.createElement(DocBookUtilities.TOPIC_ROOT_TITLE_NODE_NAME);
        
        /* 
         * Attempt to parse the title as XML. If this fails
         * then just set the title as plain text.
         */
        try {
            final Document tempDoc = XMLUtilities.convertStringToDocument("<title>" + escapeForXML(titleValue) + "</title>");
            final Node titleEle = doc.importNode(tempDoc.getDocumentElement(), true);

            // Add the child elements to the ulink node
            final NodeList nodes = titleEle.getChildNodes();
            while (nodes.getLength() > 0) {
                newTitle.appendChild(nodes.item(0));
            }
        } catch (Exception e) {
            newTitle.appendChild(doc.createTextNode(titleValue));
        }

        final Element docElement = doc.getDocumentElement();
        if (docElement != null) {
            final NodeList titleNodes = docElement.getElementsByTagName(DocBookUtilities.TOPIC_ROOT_TITLE_NODE_NAME);
            // See if we have a title node whose parent is the section
            if (titleNodes.getLength() != 0 && titleNodes.item(0).getParentNode().equals(docElement)) {
                final Node title = titleNodes.item(0);
                title.getParentNode().replaceChild(newTitle, title);
            } else {
                docElement.appendChild(newTitle);
            }
        }
    }

    /**
     * Escapes a String so that it can be used in a Docbook Element, ensuring that any entities or elements are maintained.
     *
     * @param content The string to be escaped.
     * @return The escaped string that can be used in XML.
     */
    public static String escapeForXML(final String content) {
        if (content == null) return "";

        /*
         * Note: The following characters should be escaped: & < > " '
         *
         * However, all but ampersand pose issues when other elements are included in the title.
         *
         * eg <title>Product A > Product B<phrase condition="beta">-Beta</phrase></title>
         *
         * should become
         *
         * <title>Product A &gt; Product B<phrase condition="beta">-Beta</phrase></title>
         */

        String fixedContent = XMLUtilities.STANDALONE_AMPERSAND_PATTERN.matcher(content).replaceAll("&amp;");

        // Loop over and find all the XML Elements as they should remain untouched.
        final LinkedList<String> elements = new LinkedList<String>();
        if (fixedContent.indexOf('<') != -1) {
            int index = -1;
            while ((index = fixedContent.indexOf('<', index + 1)) != -1) {
                int endIndex = fixedContent.indexOf('>', index);
                int nextIndex = fixedContent.indexOf('<', index + 1);

                /*
                  * If the next opening tag is less than the next ending tag, than the current opening tag isn't a match for the next
                  * ending tag, so continue to the next one
                  */
                if (endIndex == -1 || (nextIndex != -1 && nextIndex < endIndex)) {
                    continue;
                } else if (index + 1 == endIndex) {
                    // This is a <> sequence, so it should be ignored as well.
                    continue;
                } else {
                    elements.add(fixedContent.substring(index, endIndex + 1));
                }

            }
        }

        // Find all the elements and replace them with a marker
        String escapedTitle = fixedContent;
        for (int count = 0; count < elements.size(); count++) {
            escapedTitle = escapedTitle.replace(elements.get(count), "###" + count + "###");
        }

        // Perform the replacements on what's left
        escapedTitle = escapedTitle.replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");

        // Replace the markers
        for (int count = 0; count < elements.size(); count++) {
            escapedTitle = escapedTitle.replace("###" + count + "###", elements.get(count));
        }

        return escapedTitle;
    }

    public static void setInfo(final DocBookVersion docBookVersion, final Element info, final Element parentNode) {
        assert parentNode != null : "The parentNode parameter can not be null";
        assert info != null : "The info parameter can not be null";

        if (parentNode != null) {
            final NodeList infoNodes = parentNode.getElementsByTagName(info.getNodeName());
            // See if we have an info node whose parent is the section
            if (infoNodes.getLength() != 0 && infoNodes.item(0).getParentNode().equals(parentNode)) {
                final Node sectionInfoNode = infoNodes.item(0);
                sectionInfoNode.getParentNode().replaceChild(info, sectionInfoNode);
            } else {
                // Find the first node that isn't text or a comment
                Node firstNode = parentNode.getFirstChild();
                while (firstNode != null && firstNode.getNodeType() != Node.ELEMENT_NODE) {
                    firstNode = firstNode.getNextSibling();
                }

                // DocBook 5.0+ changed where the info node needs to be. In 5.0+ it is after the title,
                // whereas 4.5 has to be before the title.
                if (docBookVersion == DocBookVersion.DOCBOOK_50) {
                    // Set the section title based on if the first node is a info node.
                    if (firstNode != null && firstNode.getNodeName().equals(DocBookUtilities.TOPIC_ROOT_TITLE_NODE_NAME)) {
                        final Node nextNode = firstNode.getNextSibling();
                        if (nextNode != null) {
                            parentNode.insertBefore(info, nextNode);
                        } else {
                            parentNode.appendChild(info);
                        }
                    } else if (firstNode != null) {
                        parentNode.insertBefore(info, firstNode);
                    } else {
                        parentNode.appendChild(info);
                    }
                } else {
                    if (firstNode != null) {
                        parentNode.insertBefore(info, firstNode);
                    } else {
                        parentNode.appendChild(info);
                    }
                }
            }
        }
    }

    public static String buildChapter(final String contents, final String title) {
        return buildChapter(contents, title, null);
    }

    public static String buildChapter(final String contents, final String title, final String id) {
        final String titleContents = title == null || title.length() == 0 ? "" : title;
        final String chapterContents = contents == null || contents.length() == 0 ? "" : contents;
        final String idAttribute = id == null || id.length() == 0 ? "" : " id=\"" + id + "\"";
        return "<chapter" + idAttribute + "><title>" + titleContents + "</title>" + chapterContents + "</chapter>";
    }

    public static String buildAppendix(final String contents, final String title) {
        return buildAppendix(contents, title, null);
    }

    public static String buildAppendix(final String contents, final String title, final String id) {
        final String titleContents = title == null || title.length() == 0 ? "" : title;
        final String chapterContents = contents == null || contents.length() == 0 ? "" : contents;
        final String idAttribute = id == null || id.length() == 0 ? "" : " id=\"" + id + "\"";
        return "<appendix" + idAttribute + "><title>" + titleContents + "</title>" + chapterContents + "</appendix>";
    }

    public static String buildCleanSection(final String contents, final String title) {
        return buildCleanSection(contents, title, null);
    }

    public static String buildCleanSection(final String contents, final String title, final String id) {
        final String titleContents = title == null || title.length() == 0 ? "" : title;
        final String chapterContents = contents == null || contents.length() == 0 ? "" : contents;
        final String idAttribute = id == null || id.length() == 0 ? "" : " id=\"" + id + "\"";
        return "<section" + idAttribute + "><title>" + titleContents + "</title>" + chapterContents + "</section>";
    }

    public static String addDocBook45Doctype(final String xml) {
        return addDocBook45Doctype(xml, null, "chapter");
    }

    public static String addDocBook45Doctype(final String xml, final String entityFileName, final String rootElementName) {
        return XMLUtilities.addPublicDoctype(xml, "-//OASIS//DTD DocBook XML V4.5//EN",
                "http://www.oasis-open.org/docbook/xml/4.5/docbookx.dtd", entityFileName, rootElementName);
    }

    public static String addDocBook50Doctype(final String xml) {
        return addDocBook50Doctype(xml, null, "chapter");
    }

    public static String addDocBook50Doctype(final String xml, final String entityFileName, final String rootElementName) {
        return XMLUtilities.addPublicDoctype(xml, "-//OASIS//DTD DocBook XML V5.0//EN",
                "http://www.oasis-open.org/docbook/xml/5.0/docbookx.dtd", entityFileName, rootElementName);
    }

    public static String addDocBook50Namespace(final String xml) {
        // Find the root element name
        final String rootEleName = XMLUtilities.findRootElementName(xml);
        return addDocBook50Namespace(xml, rootEleName);
    }

    public static void addNamespaceToDocElement(final DocBookVersion version, final Document doc) {
        if (version == null) throw new IllegalArgumentException("version cannot be null");
        if (version == DocBookVersion.DOCBOOK_50) {
            addDocBook50NamespaceToDocElement(doc);
        }
    }

    public static void addDocBook50NamespaceToDocElement(final Document doc) {
        doc.getDocumentElement().setAttribute("xmlns", "http://docbook.org/ns/docbook");
        doc.getDocumentElement().setAttribute("xmlns:xlink", "http://www.w3.org/1999/xlink");
        doc.getDocumentElement().setAttribute("version", "5.0");
    }

    public static String buildDocBookDoctype(final DocBookVersion version, final String rootElementName, final String entities,
            final boolean includeDTD) {
        if (version == null) throw new IllegalArgumentException("format cannot be null");

        if (version == DocBookVersion.DOCBOOK_45) {
            return buildDocBook45Doctype(rootElementName, entities, includeDTD);
        }

        if (version == DocBookVersion.DOCBOOK_50) {
            return buildDocBook50Doctype(rootElementName, entities, includeDTD);
        }

        return "";
    }

    public static String buildDocBook50Doctype(final String rootElementName, final String entities, final boolean includeDTD) {
        if (rootElementName == null) throw new IllegalArgumentException("rootElementName cannot be null");
        final StringBuilder retValue = new StringBuilder();
        retValue.append("<!DOCTYPE " + rootElementName);
        if (includeDTD) {
            retValue.append(" PUBLIC \"-//OASIS//DTD DocBook XML V5.0//EN\" \"http://www.oasis-open.org/docbook/xml/5.0/docbookx.dtd\"");
        }
        ;
        retValue.append(" [\n");
        if (entities != null) {
            retValue.append(entities);
        }
        retValue.append("]>\n");
        return retValue.toString();
    }

    public static String buildDocBook45Doctype(final String rootElementName, final String entities, final boolean includeDTD) {
        if (rootElementName == null) throw new IllegalArgumentException("rootElementName cannot be null");
        final StringBuilder retValue = new StringBuilder();
        retValue.append("<!DOCTYPE " + rootElementName);
        if (includeDTD) {
            retValue.append(" PUBLIC \"-//OASIS//DTD DocBook XML V4.5//EN\" \"http://www.oasis-open.org/docbook/xml/4.5/docbookx.dtd\"");
        }
        ;
        retValue.append(" [\n");
        if (entities != null) {
            retValue.append(entities);
        }
        retValue.append("]>\n");
        return retValue.toString();
    }

    public static String addDocBook50Namespace(final String xml, final String rootElementName) {
        if (rootElementName == null) throw new IllegalArgumentException("rootElementName cannot be null");
        final Pattern pattern = Pattern.compile("(?<ELEMENT><" + rootElementName + ".*?)>");
        final Matcher matcher = pattern.matcher(xml);
        if (matcher.find()) {
            final String element = matcher.group("ELEMENT");
            // Remove any current namespace declaration
            String fixedElement = element.replaceFirst(" xmlns\\s*=\\s*('|\").*?('|\")", "");
            // Remove any current version declaration
            fixedElement = fixedElement.replaceFirst(" version\\s*=\\s*('|\").*?('|\")", "");
            // Remove any current xlink namespace declaration
            fixedElement = fixedElement.replaceFirst(" xmlns:xlink\\s*=\\s*('|\").*?('|\")", "");
            return xml.replaceFirst(element,
                    fixedElement + " xmlns=\"http://docbook.org/ns/docbook\" xmlns:xlink=\"http://www.w3.org/1999/xlink\"" +
                            " version=\"5.0\"");
        } else {
            return xml;
        }
    }

    public static String buildXRefListItem(final String xref, final String role) {
        final String roleAttribute = role == null || role.length() == 0 ? "" : " role=\"" + role + "\"";
        return "<listitem><para><xref" + roleAttribute + " linkend=\"" + xref + "\"/></para></listitem>";
    }

    public static List<Element> buildXRef(final Document xmlDoc, final String xref) {
        return buildXRef(xmlDoc, xref, null);
    }

    public static List<Element> buildXRef(final Document xmlDoc, final String xref, final String xrefStyle) {
        final List<Element> retValue = new ArrayList<Element>();

        final Element xrefItem = xmlDoc.createElement("xref");
        xrefItem.setAttribute("linkend", xref);

        if (xrefStyle != null && !xrefStyle.isEmpty()) {
            xrefItem.setAttribute("xrefstyle", xrefStyle);
        }

        retValue.add(xrefItem);

        return retValue;
    }

    public static String buildXRef(final String xref) {
        return "<xref linkend=\"" + xref + "\" />";
    }

    public static String buildXRef(final String xref, final String xrefStyle) {
        return "<xref linkend=\"" + xref + "\" xrefstyle=\"" + xrefStyle + "\" />";
    }

    public static String buildLink(final String xref, final String style, final String value) {
        return "<link linkend=\"" + xref + "\" xrefstyle=\"" + style + "\">" + value + "</link>";
    }

    public static List<Element> buildULink(final Document xmlDoc, final String url, final String label) {
        final List<Element> retValue = new ArrayList<Element>();

        final Element ulinkItem = xmlDoc.createElement("ulink");
        ulinkItem.setAttribute("url", url);

        final Text labelElement = xmlDoc.createTextNode(label);
        ulinkItem.appendChild(labelElement);

        retValue.add(ulinkItem);

        return retValue;
    }

    public static String buildULink(final String url, final String label) {
        return "<ulink url=\"" + url + "\">" + label + "</ulink>";
    }

    public static String buildULinkListItem(final String url, final String label) {
        return "<listitem><para><ulink url=\"" + url + "\">" + label + "</ulink></para></listitem>";
    }

    public static List<Element> buildEmphasisPrefixedXRef(final Document xmlDoc, final String prefix, final String xref) {
        final List<Element> retValue = new ArrayList<Element>();

        final Element emphasis = xmlDoc.createElement("emphasis");
        emphasis.setTextContent(prefix);
        retValue.add(emphasis);

        final Element xrefItem = xmlDoc.createElement("xref");
        xrefItem.setAttribute("linkend", xref);
        retValue.add(xrefItem);

        return retValue;
    }

    public static List<Element> buildEmphasisPrefixedULink(final Document xmlDoc, final String prefix, final String url,
            final String label) {
        final List<Element> retValue = new ArrayList<Element>();

        final Element emphasis = xmlDoc.createElement("emphasis");
        emphasis.setTextContent(prefix);
        retValue.add(emphasis);

        final Element xrefItem = xmlDoc.createElement("ulink");
        xrefItem.setAttribute("url", url);
        retValue.add(xrefItem);

        final Text labelElement = xmlDoc.createTextNode(label);
        xrefItem.appendChild(labelElement);

        return retValue;
    }

    public static Node buildDOMXRefLinkListItem(final String xref, final String title, final Document xmlDoc) {
        final Element listItem = xmlDoc.createElement("listitem");

        final Element paraItem = xmlDoc.createElement("para");
        listItem.appendChild(paraItem);

        final Element linkItem = xmlDoc.createElement("link");
        linkItem.setAttribute("linkend", xref);
        linkItem.setTextContent(title);
        paraItem.appendChild(linkItem);

        return listItem;
    }

    public static Node buildDOMLinkListItem(final List<Node> children, final Document xmlDoc) {
        final Element listItem = xmlDoc.createElement("listitem");

        final Element paraItem = xmlDoc.createElement("para");
        listItem.appendChild(paraItem);

        for (final Node node : children)
            paraItem.appendChild(node);

        return listItem;
    }

    public static Node buildDOMXRef(final String xref, final String title, final Document xmlDoc) {
        final Element linkItem = xmlDoc.createElement("link");
        linkItem.setAttribute("linkend", xref);
        linkItem.setTextContent(title);
        return linkItem;
    }

    public static Node buildDOMText(final String title, final Document xmlDoc) {
        final Node textNode = xmlDoc.createTextNode(title);
        return textNode;
    }

    public static String buildListItem(final String text) {
        return "<listitem><para>" + text + "</para></listitem>\n";
    }

    public static Element buildDOMListItem(final Document doc, final String text) {
        final Element listItem = doc.createElement("listitem");
        final Element para = doc.createElement("para");
        para.setTextContent(text);
        listItem.appendChild(para);
        return listItem;
    }

    public static String buildSection(final String contents, final String title) {
        return buildSection(contents, title, null, null, null);
    }

    public static String buildSection(final String contents, final String title, final String id) {
        return buildSection(contents, title, id, null, null);
    }

    public static String buildSection(final String contents, final String title, final String id, final String titleRole) {
        return buildSection(contents, title, id, titleRole, null);
    }

    public static String buildSection(final String contents, final String title, final String id, final String titleRole,
            final String xreflabel) {
        final String idAttribute = id == null || id.length() == 0 ? "" : " id=\"" + id + "\"";
        final String xreflabelAttribute = xreflabel == null || xreflabel.length() == 0 ? "" : " xreflabel=\"" + xreflabel + "\"";
        final String titleRoleAttribute = titleRole == null || titleRole.length() == 0 ? "" : " role=\"" + titleRole + "\"";

        return "<section" + idAttribute + xreflabelAttribute + ">\n" +
                "<title" + titleRoleAttribute + ">" + title + "</title>\n" +
                contents + "\n" +
                "</section>\n";
    }

    public static String wrapInListItem(final String content) {
        return "<listitem>" + content + "</listitem>";
    }

    public static String wrapListItems(final List<String> listItems) {
        return wrapListItems(null, listItems, null, null);
    }

    public static String wrapListItems(final List<String> listItems, final String title) {
        return wrapListItems(null, listItems, title, null);
    }

    public static String wrapListItems(final DocBookVersion docBookVersion, final List<String> listItems, final String title,
            final String id) {
        final String idAttribute;
        if (docBookVersion == DocBookVersion.DOCBOOK_50) {
            idAttribute = id != null && id.length() != 0 ? " xml:id=\"" + id + "\" " : "";
        } else {
            idAttribute = id != null && id.length() != 0 ? " id=\"" + id + "\" " : "";
        }
        final String titleElement = title == null || title.length() == 0 ? "" : "<title>" + title + "</title>";

        final StringBuilder retValue = new StringBuilder("<itemizedlist" + idAttribute + ">" + titleElement);
        for (final String listItem : listItems)
            retValue.append(listItem);
        retValue.append("</itemizedlist>");

        return retValue.toString();
    }

    public static String wrapListItemsInPara(final String listItems) {
        if (listItems.length() != 0) {
            return "<para>" +
                    "<itemizedlist>\n" + listItems + "</itemizedlist>" +
                    "</para>";
        }

        return "";
    }

    public static String wrapInPara(final String contents) {
        return wrapInPara(contents, null, null);
    }

    public static String wrapInPara(final String contents, final String role) {
        return wrapInPara(contents, role, null);
    }

    public static String wrapInPara(final String contents, final String role, final String id) {
        final String idAttribute = id == null || id.length() == 0 ? "" : " id=\"" + id + "\"";
        final String roleAttribute = role == null || role.length() == 0 ? "" : " role=\"" + role + "\"";
        return "<para" + idAttribute + roleAttribute + ">" +
                contents +
                "</para>";
    }

    public static List<Element> wrapItemizedListItemsInPara(final Document xmlDoc, final List<List<Element>> items) {
        final List<Element> retValue = new ArrayList<Element>();

        final Element para = xmlDoc.createElement("para");

        final Element itemizedlist = xmlDoc.createElement("itemizedlist");
        para.appendChild(itemizedlist);

        for (final List<Element> itemSequence : items) {
            final Element listitem = xmlDoc.createElement("listitem");
            itemizedlist.appendChild(listitem);

            final Element listItemPara = xmlDoc.createElement("para");
            listitem.appendChild(listItemPara);

            for (final Element item : itemSequence) {
                listItemPara.appendChild(item);
            }
        }

        retValue.add(para);

        return retValue;
    }

    public static List<Element> wrapOrderedListItemsInPara(final Document xmlDoc, final List<List<Element>> items) {
        final List<Element> retValue = new ArrayList<Element>();

        final Element para = xmlDoc.createElement("para");

        final Element orderedlist = xmlDoc.createElement("orderedlist");
        para.appendChild(orderedlist);

        for (final List<Element> itemSequence : items) {
            final Element listitem = xmlDoc.createElement("listitem");
            orderedlist.appendChild(listitem);

            final Element listItemPara = xmlDoc.createElement("para");
            listitem.appendChild(listItemPara);

            for (final Element item : itemSequence) {
                listItemPara.appendChild(item);
            }
        }

        retValue.add(para);

        return retValue;
    }

    public static List<Element> wrapItemsInListItems(final Document xmlDoc, final List<List<Element>> items) {
        final List<Element> retValue = new ArrayList<Element>();

        for (final List<Element> itemSequence : items) {
            final Element listitem = xmlDoc.createElement("listitem");
            final Element listItemPara = xmlDoc.createElement("para");
            listitem.appendChild(listItemPara);

            for (final Element item : itemSequence) {
                listItemPara.appendChild(item);
            }

            retValue.add(listitem);
        }

        return retValue;
    }

    public static String wrapInSimpleSect(final String contents) {
        return wrapInSimpleSect(contents, null, null);
    }

    public static String wrapInSimpleSect(final String contents, final String role) {
        return wrapInSimpleSect(contents, null, null);
    }

    public static String wrapInSimpleSect(final String contents, final String role, final String id) {
        final String roleAttribute = role == null || role.length() == 0 ? "" : " role=\"" + role + "\"";
        final String idAttribute = id == null || id.length() == 0 ? "" : " id=\"" + id + "\"";

        return "<simplesect" + idAttribute + roleAttribute + ">\n" +
                "\t<title></title>\n" +
                contents + "\n" +
                "</simplesect>";
    }

    public static Element wrapListItems(final Document xmlDoc, final List<Node> listItems) {
        return wrapListItems(xmlDoc, listItems, null);
    }

    public static Element wrapListItems(final Document xmlDoc, final List<Node> listItems, final String title) {
        final Element paraElement = xmlDoc.createElement("para");

        final Element itemizedlistElement = xmlDoc.createElement("itemizedlist");
        paraElement.appendChild(itemizedlistElement);

        if (title != null) {
            final Element titleElement = xmlDoc.createElement("title");
            itemizedlistElement.appendChild(titleElement);
            titleElement.setTextContent(title);
        }

        for (final Node listItem : listItems)
            itemizedlistElement.appendChild(listItem);

        return paraElement;
    }

    public static void insertNodeAfter(final Node reference, final Node insert) {
        final Node parent = reference.getParentNode();
        final Node nextSibling = reference.getNextSibling();

        if (parent == null) return;

        if (nextSibling != null) parent.insertBefore(insert, nextSibling);
        else parent.appendChild(insert);
    }

    public static Node createRelatedTopicXRef(final Document xmlDoc, final String xref, final Node parent) {
        final Element listItem = xmlDoc.createElement("listitem");
        if (parent != null) parent.appendChild(listItem);

        final Element paraItem = xmlDoc.createElement("para");
        listItem.appendChild(paraItem);

        final Element xrefItem = xmlDoc.createElement("xref");
        xrefItem.setAttribute("linkend", xref);
        paraItem.appendChild(xrefItem);

        return listItem;
    }

    public static Node createRelatedTopicXRef(final Document xmlDoc, final String xref) {
        return createRelatedTopicXRef(xmlDoc, xref, null);
    }

    public static Node createRelatedTopicULink(final Document xmlDoc, final String url, final String title, final Node parent) {
        final Element listItem = xmlDoc.createElement("listitem");
        if (parent != null) parent.appendChild(listItem);

        final Element paraItem = xmlDoc.createElement("para");
        listItem.appendChild(paraItem);

        final Element xrefItem = xmlDoc.createElement("ulink");
        xrefItem.setAttribute("url", url);
        paraItem.appendChild(xrefItem);

        // Attempt to parse the title as XML. If this fails then just set the title as plain text.
        try {
            final Document doc = XMLUtilities.convertStringToDocument("<title>" + title + "</title>");
            final Node titleEle = xmlDoc.importNode(doc.getDocumentElement(), true);

            // Add the child elements to the ulink node
            final NodeList nodes = titleEle.getChildNodes();
            while (nodes.getLength() > 0) {
                xrefItem.appendChild(nodes.item(0));
            }
        } catch (Exception e) {
            final Text labelElement = xmlDoc.createTextNode(title);
            xrefItem.appendChild(labelElement);
        }

        return listItem;
    }

    public static Node createRelatedTopicULink(final Document xmlDoc, final String url, final String title) {
        return createRelatedTopicULink(xmlDoc, url, title, null);
    }

    public static Node createRelatedTopicItemizedList(final Document xmlDoc, final String title) {
        final Node itemizedlist = xmlDoc.createElement("itemizedlist");
        final Node itemizedlistTitle = xmlDoc.createElement("title");
        itemizedlistTitle.setTextContent(title);
        itemizedlist.appendChild(itemizedlistTitle);

        return itemizedlist;
    }

    public static Document wrapDocumentInSection(final Document doc) {
        return wrapDocument(doc, "section");
    }

    public static Document wrapDocumentInAppendix(final Document doc) {
        return wrapDocument(doc, "appendix");
    }

    public static Document wrapDocumentInLegalNotice(final Document doc) {
        return wrapDocument(doc, "legalnotice");
    }

    public static Document wrapDocumentInAuthorGroup(final Document doc) {
        return wrapDocument(doc, "authorgroup");
    }

    public static Document wrapDocument(final Document doc, final String elementName) {
        if (!doc.getDocumentElement().getNodeName().equals(elementName)) {
            final Element originalDocumentElement = doc.getDocumentElement();
            final Element newDocumentElement;
            if (doc.getDocumentElement().getNamespaceURI() != null) {
                newDocumentElement = doc.createElementNS(doc.getDocumentElement().getNamespaceURI(), elementName);
            } else {
                newDocumentElement = doc.createElement(elementName);
            }

            // Copy all children
            NodeList children = originalDocumentElement.getChildNodes();
            while (children.getLength() != 0) {
                newDocumentElement.appendChild(children.item(0));
            }

            // Copy all the attributes
            NamedNodeMap attrs = originalDocumentElement.getAttributes();
            for (int i = 0; i < attrs.getLength(); i++) {
                final Attr attr = (Attr) attrs.item(i);
                originalDocumentElement.removeAttributeNode(attr);
                newDocumentElement.setAttributeNode(attr);
            }

            // Replace the original element
            doc.replaceChild(newDocumentElement, originalDocumentElement);

            return doc;
        } else {
            return doc;
        }
    }

    /**
     * Wrap a list of Strings in a {@code<row>} element. Each string
     * is also wrapped in a {@code<entry>} element.
     *
     * @param items The list of items to be set in the table row.
     * @return The strings wrapped in row and entry elements.
     */
    public static String wrapInTableRow(final List<String> items) {
        final StringBuilder output = new StringBuilder("<row>");
        for (final String entry : items) {
            output.append("<entry>" + entry + "</entry>");
        }

        output.append("</row>");
        return output.toString();
    }

    public static String wrapInTable(final String title, final List<List<String>> rows) {
        return wrapInTable(title, null, null, rows);
    }

    public static String wrapInTable(final String title, final List<String> headers, final List<List<String>> rows) {
        return wrapInTable(title, headers, null, rows);
    }

    public static String wrapInTable(final String title, final List<String> headers, final List<String> footers,
            final List<List<String>> rows) {
        if (rows == null) throw new IllegalArgumentException("rows cannot be null");

        final StringBuilder output = new StringBuilder("<table>\n");
        output.append("\t<title>" + title + "</title>\n");

        final int numColumns = headers == null ? (rows == null || rows.size() == 0 ? 0 : rows.get(0).size()) : Math.max(headers.size(),
                (rows == null || rows.size() == 0 ? 0 : rows.get(0).size()));
        output.append("\t<tgroup cols=\"" + numColumns + "\">\n");
        // Add the headers
        if (headers != null && !headers.isEmpty()) {
            output.append("\t\t<thead>\n");
            output.append("\t\t\t" + wrapInTableRow(headers));
            output.append("\t\t</thead>\n");
        }

        // Add the footer
        if (footers != null && !footers.isEmpty()) {
            output.append("\t\t<tfoot>\n");
            output.append("\t\t\t" + wrapInTableRow(footers));
            output.append("\t\t</tfoot>\n");
        }

        // Create the table body
        output.append("\t\t<tbody>\n");
        for (final List<String> row : rows) {
            output.append("\t\t\t" + wrapInTableRow(row));
        }
        output.append("\t\t</tbody>\n");
        output.append("\t</tgroup>\n");
        output.append("</table>\n");
        return output.toString();
    }

    public static String wrapInGlossTerm(final String glossTerm) {
        return "<glossterm>" + glossTerm + "</glossterm>";
    }

    /**
     * Creates a Glossary Definition element that contains an itemized list.
     * Each item specified in the items list is wrapped in a {@code<para>} and
     * {@code<listitem>} element and then added to the itemizedlist.
     *
     * @param title The title for the itemized list.
     * @param items The list of items that should be created in the list.
     * @return The {@code<glossdef>} wrapped list of items.
     */
    public static String wrapInItemizedGlossDef(final String title, final List<String> items) {
        final List<String> itemizedList = new ArrayList<String>();
        for (final String listItem : items) {
            itemizedList.add(wrapInListItem(wrapInPara(listItem)));
        }
        return "<glossdef>" + DocBookUtilities.wrapListItems(itemizedList) + "</glossdef>";
    }

    public static String wrapInGlossEntry(final String glossTerm, final String glossDef) {
        return "<glossentry>" + glossTerm + glossDef + "</glossentry>";
    }

    /**
     * Check to ensure that a table isn't missing any entries in its rows.
     *
     * @param table The DOM table node to be checked.
     * @return True if the table has the required number of entries, otherwise false.
     */
    public static boolean validateTableRows(final Element table) {
        assert table != null;
        assert table.getNodeName().equals("table") || table.getNodeName().equals("informaltable");

        final NodeList tgroups = table.getElementsByTagName("tgroup");
        for (int i = 0; i < tgroups.getLength(); i++) {
            final Element tgroup = (Element) tgroups.item(i);
            if (!validateTableGroup(tgroup)) return false;
        }

        return true;
    }

    /**
     * Check to ensure that a Docbook tgroup isn't missing an row entries, using number of cols defined for the tgroup.
     *
     * @param tgroup The DOM tgroup element to be checked.
     * @return True if the tgroup has the required number of entries, otherwise false.
     */
    public static boolean validateTableGroup(final Element tgroup) {
        assert tgroup != null;
        assert tgroup.getNodeName().equals("tgroup");

        final Integer numColumns = Integer.parseInt(tgroup.getAttribute("cols"));

        // Check that all the thead, tbody and tfoot elements have the correct number of entries.
        final List<Node> nodes = XMLUtilities.getDirectChildNodes(tgroup, "thead", "tbody", "tfoot");
        for (final Node ele : nodes) {
            // Find all child nodes that are a row
            final NodeList children = ele.getChildNodes();
            for (int i = 0; i < children.getLength(); i++) {
                final Node node = children.item(i);
                if (node.getNodeName().equals("row") || node.getNodeName().equals("tr")) {
                    if (!validateTableRow(node, numColumns)) return false;
                }
            }
        }

        return true;
    }

    /**
     * Check to ensure that a docbook row has the required number of columns for a table.
     *
     * @param row        The DOM row element to be checked.
     * @param numColumns The number of entry elements that should exist in the row.
     * @return True if the row has the required number of entries, otherwise false.
     */
    public static boolean validateTableRow(final Node row, final int numColumns) {
        assert row != null;
        assert row.getNodeName().equals("row") || row.getNodeName().equals("tr");

        if (row.getNodeName().equals("row")) {
            final List<Node> entries = XMLUtilities.getDirectChildNodes(row, "entry");
            final List<Node> entryTbls = XMLUtilities.getDirectChildNodes(row, "entrytbl");

            if ((entries.size() + entryTbls.size()) <= numColumns) {
                for (final Node entryTbl : entryTbls) {
                    if (!validateEntryTbl((Element) entryTbl)) return false;
                }
                return true;
            } else {
                return false;
            }
        } else {
            final List<Node> nodes = XMLUtilities.getDirectChildNodes(row, "td", "th");

            return nodes.size() <= numColumns;
        }
    }

    /**
     * Check to ensure that a Docbook entrytbl isn't missing an row entries, using number of cols defined for the entrytbl.
     *
     * @param entryTbl The DOM entrytbl element to be checked.
     * @return True if the entryTbl has the required number of entries, otherwise false.
     */
    public static boolean validateEntryTbl(final Element entryTbl) {
        assert entryTbl != null;
        assert entryTbl.getNodeName().equals("entrytbl");

        final Integer numColumns = Integer.parseInt(entryTbl.getAttribute("cols"));

        // Check that all the thead and tbody elements have the correct number of entries.
        final List<Node> nodes = XMLUtilities.getDirectChildNodes(entryTbl, "thead", "tbody");
        for (final Node ele : nodes) {
            // Find all child nodes that are a row
            final NodeList children = ele.getChildNodes();
            for (int i = 0; i < children.getLength(); i++) {
                final Node node = children.item(i);
                if (node.getNodeName().equals("row") || node.getNodeName().equals("tr")) {
                    if (!validateTableRow(node, numColumns)) return false;
                }
            }
        }

        return true;
    }

    /**
     * Check the XML Document and it's children for condition
     * statements. If any are found then check if the condition
     * matches the passed condition string. If they don't match
     * then remove the nodes.
     *
     * @param condition The condition regex to be tested against.
     * @param doc       The Document to check for conditional statements.
     */
    public static void processConditions(final String condition, final Document doc) {
        processConditions(condition, doc, "default");
    }

    /**
     * Check the XML Document and it's children for condition
     * statements. If any are found then check if the condition
     * matches the passed condition string. If they don't match
     * then remove the nodes.
     *
     * @param condition        The condition regex to be tested against.
     * @param doc              The Document to check for conditional statements.
     * @param defaultCondition The default condition to allow a default block when processing conditions.
     */
    public static void processConditions(final String condition, final Document doc, final String defaultCondition) {
        processConditions(condition, doc, defaultCondition, true);
    }

    /**
     * Check the XML Document and it's children for condition
     * statements. If any are found then check if the condition
     * matches the passed condition string. If they don't match
     * then remove the nodes.
     *
     * @param condition           The condition regex to be tested against.
     * @param doc                 The Document to check for conditional statements.
     * @param defaultCondition    The default condition to allow a default block when processing conditions.
     * @param removeConditionAttr Remove the condition attribute from any matching/leftover nodes.
     */
    public static void processConditions(final String condition, final Document doc, final String defaultCondition,
            boolean removeConditionAttr) {
        final Map<Node, List<String>> conditionalNodes = getConditionNodes(doc.getDocumentElement());

        // Loop through each condition found and see if it matches
        for (final Map.Entry<Node, List<String>> entry : conditionalNodes.entrySet()) {
            final Node node = entry.getKey();
            final List<String> nodeConditions = entry.getValue();
            boolean matched = false;

            // Check to see if the condition matches
            for (final String nodeCondition : nodeConditions) {
                if (condition != null && nodeCondition.matches(condition)) {
                    matched = true;
                } else if (condition == null && defaultCondition != null && nodeCondition.matches(defaultCondition)) {
                    matched = true;
                }
            }

            // If there was no match then remove the node
            if (!matched) {
                final Node parentNode = node.getParentNode();
                if (parentNode != null) {
                    parentNode.removeChild(node);
                }
            } else if (removeConditionAttr) {
                // Remove the condition attribute so that it can't get processed by something else downstream
                ((Element) node).removeAttribute("condition");
            }
        }
    }

    /**
     * Collects any nodes that have the "condition" attribute in the
     * passed node or any of it's children nodes.
     *
     * @param node The node to collect condition elements from.
     * @return A mapping of nodes to their conditions.
     */
    public static Map<Node, List<String>> getConditionNodes(final Node node) {
        final Map<Node, List<String>> conditionalNodes = new HashMap<Node, List<String>>();
        getConditionNodes(node, conditionalNodes);
        return conditionalNodes;
    }

    /**
     * Collects any nodes that have the "condition" attribute in the
     * passed node or any of it's children nodes.
     *
     * @param node             The node to collect condition elements from.
     * @param conditionalNodes A mapping of nodes to their conditions
     */
    private static void getConditionNodes(final Node node, final Map<Node, List<String>> conditionalNodes) {
        final NamedNodeMap attributes = node.getAttributes();
        if (attributes != null) {
            final Node attr = attributes.getNamedItem("condition");

            if (attr != null) {
                final String conditionStatement = attr.getNodeValue();

                final String[] conditions = conditionStatement.split("\\s*(;|,)\\s*");

                conditionalNodes.put(node, Arrays.asList(conditions));
            }
        }

        // Check the child nodes for condition attributes
        final NodeList elements = node.getChildNodes();
        for (int i = 0; i < elements.getLength(); ++i) {
            getConditionNodes(elements.item(i), conditionalNodes);
        }
    }

    /**
     * Get the Translatable Strings from an XML Document. This method will return of Translation strings to XML DOM nodes within
     * the XML Document. <br />
     * <br />
     * Note: This function has a flaw when breaking up strings if the Child Nodes contain translatable elements.
     *
     * @param xml             The XML to get the translatable strings from.
     * @param allowDuplicates If duplicate translation strings should be created in the returned list.
     * @return A list of StringToNodeCollection objects containing the translation strings and nodes.
     */
    @Deprecated
    public static List<StringToNodeCollection> getTranslatableStringsV1(final Document xml, final boolean allowDuplicates) {
        if (xml == null) return null;

        final List<StringToNodeCollection> retValue = new ArrayList<StringToNodeCollection>();

        final NodeList nodes = xml.getDocumentElement().getChildNodes();
        for (int i = 0; i < nodes.getLength(); ++i) {
            final Node node = nodes.item(i);
            getTranslatableStringsFromNodeV1(node, retValue, allowDuplicates, new XMLProperties());
        }

        return retValue;
    }

    /**
     * Get the Translatable Strings from an XML Document. This method will return of Translation strings to XML DOM nodes within
     * the XML Document.
     *
     * @param xml             The XML to get the translatable strings from.
     * @param allowDuplicates If duplicate translation strings should be created in the returned list.
     * @return A list of StringToNodeCollection objects containing the translation strings and nodes.
     */
    @Deprecated
    public static List<StringToNodeCollection> getTranslatableStringsV2(final Document xml, final boolean allowDuplicates) {
        if (xml == null) return null;

        final List<StringToNodeCollection> retValue = new ArrayList<StringToNodeCollection>();

        final NodeList nodes = xml.getDocumentElement().getChildNodes();
        for (int i = 0; i < nodes.getLength(); ++i) {
            final Node node = nodes.item(i);
            getTranslatableStringsFromNodeV2(node, retValue, allowDuplicates, new XMLProperties());
        }

        return retValue;
    }

    /**
     * Get the Translatable Strings from an XML Document. This method will return of Translation strings to XML DOM nodes within
     * the XML Document.
     *
     * @param xml             The XML to get the translatable strings from.
     * @param allowDuplicates If duplicate translation strings should be created in the returned list.
     * @return A list of StringToNodeCollection objects containing the translation strings and nodes.
     */
    public static List<StringToNodeCollection> getTranslatableStringsV3(final Document xml, final boolean allowDuplicates) {
        if (xml == null) return null;

        return getTranslatableStringsV3(xml.getDocumentElement(), allowDuplicates);
    }

    /**
     * Get the Translatable Strings from an XML Node. This method will return of Translation strings to XML DOM nodes within
     * the XML Document.
     *
     * @param node             The XML to get the translatable strings from.
     * @param allowDuplicates If duplicate translation strings should be created in the returned list.
     * @return A list of StringToNodeCollection objects containing the translation strings and nodes.
     */
    public static List<StringToNodeCollection> getTranslatableStringsV3(final Node node, final boolean allowDuplicates) {
        if (node == null) return null;

        final List<StringToNodeCollection> retValue = new LinkedList<StringToNodeCollection>();

        final NodeList nodes = node.getChildNodes();
        for (int i = 0; i < nodes.getLength(); ++i) {
            final Node childNode = nodes.item(i);
            getTranslatableStringsFromNodeV3(childNode, retValue, allowDuplicates, new XMLProperties());
        }

        return retValue;
    }

    /**
     * Check if a node has child translatable elements.
     *
     * @param node The node to check for child translatable elements.
     * @return True if the node has translatable child Elements.
     */
    @Deprecated
    private static boolean doesElementContainTranslatableContentV1(final Node node) {
        final NodeList children = node.getChildNodes();
        if (children != null) {
            /* check to see if any of the children are translatable nodes */
            for (int j = 0; j < children.getLength(); ++j) {
                final Node child = children.item(j);
                final String childName = child.getNodeName();

                /* this child node is itself translatable, so return true */
                if (TRANSLATABLE_ELEMENTS_OLD.contains(childName)) return true;
            }

            /*
             * now check to see if any of the child have children that are translatable
             */
            for (int j = 0; j < children.getLength(); ++j) {
                final Node child = children.item(j);
                final NodeList grandChildren = child.getChildNodes();
                for (int k = 0; k < grandChildren.getLength(); ++k) {
                    final Node grandChild = grandChildren.item(k);
                    final boolean result = doesElementContainTranslatableContentV1(grandChild);
                    if (result) return true;
                }
            }
        }

        return false;
    }

    /**
     * Check if a node has child translatable elements.
     *
     * @param node The node to check for child translatable elements.
     * @return True if the node has translatable child Elements.
     */
    private static boolean doesElementContainTranslatableContentV2(final Node node) {
        final NodeList children = node.getChildNodes();
        if (children != null) {
            // check to see if any of the children are translatable nodes
            for (int j = 0; j < children.getLength(); ++j) {
                final Node child = children.item(j);
                final String childName = child.getNodeName();

                if (TRANSLATABLE_ELEMENTS_OLD.contains(childName)) {
                    // This child node is itself translatable, so return true
                    return true;
                } else if (doesElementContainTranslatableContentV2(child)) {
                    // check if this child contains translatable nodes
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Check if a node has child translatable elements.
     *
     * @param node The node to check for child translatable elements.
     * @return True if the node has translatable child Elements.
     */
    private static boolean doesElementContainTranslatableContentV3(final Node node) {
        final NodeList children = node.getChildNodes();
        if (children != null) {
            // check to see if any of the children are translatable nodes
            for (int j = 0; j < children.getLength(); ++j) {
                final Node child = children.item(j);
                final String childName = child.getNodeName();

                if (TRANSLATABLE_ELEMENTS.contains(childName)) {
                    // This child node is itself translatable, so return true
                    return true;
                } else if (doesElementContainTranslatableContentV3(child)) {
                    // check if this child contains translatable nodes
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Get the Translatable String to Node collections from an XML DOM Node.
     *
     * @param node               The node to get the translatable elements from.
     * @param translationStrings The list of translation StringToNodeCollection objects to add to.
     * @param allowDuplicates    If duplicate translation strings should be created in the translationStrings list.
     * @param props              A set of XML Properties for the Node.
     */
    @Deprecated
    private static void getTranslatableStringsFromNodeV1(final Node node, final List<StringToNodeCollection> translationStrings,
            final boolean allowDuplicates, final XMLProperties props) {
        if (node == null || translationStrings == null) return;

        XMLProperties xmlProperties = new XMLProperties(props);

        final String nodeName = node.getNodeName();
        final String nodeParentName = node.getParentNode() != null ? node.getParentNode().getNodeName() : null;

        final boolean translatableElement = TRANSLATABLE_ELEMENTS_OLD.contains(nodeName);
        final boolean standaloneElement = TRANSLATABLE_IF_STANDALONE_ELEMENTS.contains(nodeName);
        final boolean translatableParentElement = TRANSLATABLE_ELEMENTS_OLD.contains(nodeParentName);
        if (!xmlProperties.isInline() && INLINE_ELEMENTS.contains(nodeName)) xmlProperties.setInline(true);
        if (!xmlProperties.isVerbatim() && VERBATIM_ELEMENTS.contains(nodeName)) xmlProperties.setVerbatim(true);

        /*
         * this element has translatable strings if:
         *
         * 1. a translatableElement
         *
         * OR
         *
         * 2. a standaloneElement without a translatableParentElement
         *
         * 3. not a standaloneElement and not an inlineElement
         */

        if ((translatableElement && ((standaloneElement && !translatableParentElement) || (!standaloneElement && !xmlProperties.isInline
                ())))) {
            final NodeList children = node.getChildNodes();
            final boolean hasChildren = children == null || children.getLength() != 0;

            /* dump the node if it has no children */
            if (!hasChildren) {
                final String nodeText = XMLUtilities.convertNodeToString(node, false);
                final String cleanedNodeText = cleanTranslationText(nodeText, true, true);

                if (xmlProperties.isVerbatim()) {
                    addTranslationToNodeDetailsToCollection(nodeText, node, allowDuplicates, translationStrings);
                } else if (!cleanedNodeText.isEmpty()) {
                    addTranslationToNodeDetailsToCollection(cleanedNodeText, node, allowDuplicates, translationStrings);
                }

            }
            /*
             * dump all child nodes until we hit one that itself contains a translatable element. in effect the translation
             * strings can contain up to one level of xml elements.
             */
            else {
                ArrayList<Node> nodes = new ArrayList<Node>();
                String translatableString = "";

                final int childrenLength = children.getLength();
                for (int i = 0; i < childrenLength; ++i) {
                    final Node child = children.item(i);

                    /*
                     * does this child have another level of translatable tags?
                     */
                    final boolean containsTranslatableTags = doesElementContainTranslatableContentV1(child);

                    /*
                     * if so, save the string we have been building up, process the child, and start building up a new string
                     */
                    if (containsTranslatableTags) {
                        if (nodes.size() != 0) {
                            /*
                             * We have found a child node that itself contains some translatable children. In this case we
                             * create a new translatable string. It is possible that the translatableString has some
                             * insignificant trailing whitespace, because the call to the cleanTranslationText function in the
                             * else statement below has assumed that the node being processed was not the last one in the
                             * translatable string, making the trailing whitespace important. So we clean up the trailing
                             * whitespace here.
                             */

                            final Matcher matcher = TRAILING_WHITESPACE_RE_PATTERN.matcher(translatableString);
                            if (matcher.matches()) translatableString = matcher.group("content");

                            addTranslationToNodeDetailsToCollection(translatableString, nodes, allowDuplicates, translationStrings);

                            translatableString = "";
                            nodes = new ArrayList<Node>();
                        }

                        getTranslatableStringsFromNodeV1(child, translationStrings, allowDuplicates, xmlProperties);
                    } else {
                        final String childName = child.getNodeName();
                        final String childText = XMLUtilities.convertNodeToString(child, true, false);

                        final String cleanedChildText = cleanTranslationText(childText, i == 0, i == childrenLength - 1);
                        final boolean isVerbatimNode = VERBATIM_ELEMENTS.contains(childName);

                        final String thisTranslatableString = isVerbatimNode || xmlProperties.isVerbatim() ? childText : cleanedChildText;

                        translatableString += thisTranslatableString;
                        nodes.add(child);
                    }
                }

                /* save the last translated string */
                if (nodes.size() != 0) {
                    addTranslationToNodeDetailsToCollection(translatableString, nodes, allowDuplicates, translationStrings);

                    translatableString = "";
                }
            }
        } else {
            /* if we hit a non-translatable element, process its children */
            final NodeList nodeList = node.getChildNodes();
            for (int i = 0; i < nodeList.getLength(); ++i) {
                final Node child = nodeList.item(i);
                getTranslatableStringsFromNodeV1(child, translationStrings, allowDuplicates, xmlProperties);
            }
        }
    }

    /**
     * Get the Translatable String to Node collections from an XML DOM Node.
     *
     * @param node               The node to get the translatable elements from.
     * @param translationStrings The list of translation StringToNodeCollection objects to add to.
     * @param allowDuplicates    If duplicate translation strings should be created in the translationStrings list.
     * @param props              A set of XML Properties for the Node.
     */
    @Deprecated
    public static void getTranslatableStringsFromNodeV2(final Node node, final List<StringToNodeCollection> translationStrings,
            final boolean allowDuplicates, final XMLProperties props) {
        if (node == null || translationStrings == null) return;

        XMLProperties xmlProperties = new XMLProperties(props);

        final String nodeName = node.getNodeName();
        final String nodeParentName = node.getParentNode() != null ? node.getParentNode().getNodeName() : null;

        final boolean translatableElement = TRANSLATABLE_ELEMENTS_OLD.contains(nodeName);
        final boolean standaloneElement = TRANSLATABLE_IF_STANDALONE_ELEMENTS.contains(nodeName);
        final boolean translatableParentElement = TRANSLATABLE_ELEMENTS_OLD.contains(nodeParentName);
        if (!xmlProperties.isInline() && INLINE_ELEMENTS.contains(nodeName)) xmlProperties.setInline(true);
        if (!xmlProperties.isVerbatim() && VERBATIM_ELEMENTS.contains(nodeName)) xmlProperties.setVerbatim(true);

        /*
         * this element has translatable strings if:
         *
         * 1. a translatableElement
         *
         * OR
         *
         * 2. a standaloneElement without a translatableParentElement
         *
         * 3. not a standaloneElement and not an inlineElement
         */

        if ((translatableElement && ((standaloneElement && !translatableParentElement) || (!standaloneElement && !xmlProperties.isInline
                ())))) {
            final NodeList children = node.getChildNodes();
            final boolean hasChildren = children == null || children.getLength() != 0;

            // dump the node if it has no children
            if (!hasChildren) {
                final String nodeText = XMLUtilities.convertNodeToString(node, false);
                final String cleanedNodeText = cleanTranslationText(nodeText, true, true);

                if (xmlProperties.isVerbatim()) {
                    addTranslationToNodeDetailsToCollection(nodeText, node, allowDuplicates, translationStrings);
                } else if (!cleanedNodeText.isEmpty() && !cleanedNodeText.matches("^\\s+$")) {
                    addTranslationToNodeDetailsToCollection(cleanedNodeText, node, allowDuplicates, translationStrings);
                }

            }
            /*
             * dump all child nodes until we hit one that itself contains a translatable element. in effect the translation
             * strings can contain up to one level of xml elements.
             */
            else {
                ArrayList<Node> nodes = new ArrayList<Node>();
                String translatableString = "";
                boolean removeWhitespaceFromStart = true;

                final int childrenLength = children.getLength();
                for (int i = 0; i < childrenLength; ++i) {
                    final Node child = children.item(i);
                    final String childNodeName = child.getNodeName();

                    // does this child have another level of translatable tags?
                    final boolean containsTranslatableTags = doesElementContainTranslatableContentV2(child);
                    final boolean childTranslatableElement = TRANSLATABLE_ELEMENTS_OLD.contains(childNodeName);
                    final boolean childInlineElement = INLINE_ELEMENTS.contains(childNodeName);

                    // if so, save the string we have been building up, process the child, and start building up a new string
                    if ((containsTranslatableTags || childTranslatableElement) && !childInlineElement) {
                        if (nodes.size() != 0) {
                            /*
                             * We have found a child node that itself contains some translatable children. In this case we
                             * create a new translatable string. It is possible that the translatableString has some
                             * insignificant trailing whitespace, because the call to the cleanTranslationText function in the
                             * else statement below has assumed that the node being processed was not the last one in the
                             * translatable string, making the trailing whitespace important. So we clean up the trailing
                             * whitespace here.
                             */

                            final Matcher matcher = TRAILING_WHITESPACE_RE_PATTERN.matcher(translatableString);
                            if (matcher.matches()) translatableString = matcher.group("content");

                            addTranslationToNodeDetailsToCollection(translatableString, nodes, allowDuplicates, translationStrings);

                            translatableString = "";
                            nodes = new ArrayList<Node>();
                            removeWhitespaceFromStart = true;
                        }

                        getTranslatableStringsFromNodeV2(child, translationStrings, allowDuplicates, xmlProperties);
                    } else {
                        final String childName = child.getNodeName();
                        final String childText = XMLUtilities.convertNodeToString(child, true, false);

                        final String cleanedChildText = cleanTranslationText(childText, removeWhitespaceFromStart, i == childrenLength - 1);
                        final boolean isVerbatimNode = VERBATIM_ELEMENTS.contains(childName);

                        final String thisTranslatableString = isVerbatimNode || xmlProperties.isVerbatim() ? childText : cleanedChildText;

                        if (!thisTranslatableString.isEmpty() && !thisTranslatableString.matches("^\\s+$")) {
                            translatableString += thisTranslatableString;
                            nodes.add(child);

                            /*
                             * We've processed the first element in the string so now we don't want to remove whitespace from
                             * the start of the String
                             */
                            removeWhitespaceFromStart = false;
                        }
                    }
                }

                // save the last translated string
                if (nodes.size() != 0) {
                    addTranslationToNodeDetailsToCollection(translatableString, nodes, allowDuplicates, translationStrings);

                    translatableString = "";
                }
            }
        } else {
            // if we hit a non-translatable element, process its children
            final NodeList nodeList = node.getChildNodes();
            for (int i = 0; i < nodeList.getLength(); ++i) {
                final Node child = nodeList.item(i);
                getTranslatableStringsFromNodeV2(child, translationStrings, allowDuplicates, xmlProperties);
            }
        }
    }

    /**
     * Get the Translatable String to Node collections from an XML DOM Node.
     *
     * @param node               The node to get the translatable elements from.
     * @param translationStrings The list of translation StringToNodeCollection objects to add to.
     * @param allowDuplicates    If duplicate translation strings should be created in the translationStrings list.
     * @param props              A set of XML Properties for the Node.
     */
    public static void getTranslatableStringsFromNodeV3(final Node node, final List<StringToNodeCollection> translationStrings,
            final boolean allowDuplicates, final XMLProperties props) {
        if (node == null || translationStrings == null) return;

        XMLProperties xmlProperties = new XMLProperties(props);

        final String nodeName = node.getNodeName();
        final String nodeParentName = node.getParentNode() != null ? node.getParentNode().getNodeName() : null;

        final boolean translatableElement = TRANSLATABLE_ELEMENTS.contains(nodeName);
        final boolean standaloneElement = TRANSLATABLE_IF_STANDALONE_ELEMENTS.contains(nodeName);
        final boolean translatableParentElement = TRANSLATABLE_ELEMENTS.contains(nodeParentName);
        if (!xmlProperties.isInline() && INLINE_ELEMENTS.contains(nodeName)) xmlProperties.setInline(true);
        if (!xmlProperties.isVerbatim() && VERBATIM_ELEMENTS.contains(nodeName)) xmlProperties.setVerbatim(true);

        /*
         * this element has translatable strings if:
         *
         * 1. a translatableElement
         *
         * OR
         *
         * 2. a standaloneElement without a translatableParentElement
         *
         * 3. not a standaloneElement and not an inlineElement
         */

        if ((translatableElement && ((standaloneElement && !translatableParentElement) || (!standaloneElement && !xmlProperties.isInline
                ())))) {
            final NodeList children = node.getChildNodes();
            final boolean hasChildren = children == null || children.getLength() != 0;

            // dump the node if it has no children
            if (!hasChildren) {
                final String nodeText = XMLUtilities.convertNodeToString(node, false);
                final String cleanedNodeText = cleanTranslationText(nodeText, true, true);

                if (xmlProperties.isVerbatim()) {
                    addTranslationToNodeDetailsToCollection(nodeText, node, allowDuplicates, translationStrings);
                } else if (!cleanedNodeText.isEmpty() && !cleanedNodeText.matches("^\\s+$")) {
                    addTranslationToNodeDetailsToCollection(cleanedNodeText, node, allowDuplicates, translationStrings);
                }

            }
            /*
             * dump all child nodes until we hit one that itself contains a translatable element. in effect the translation
             * strings can contain up to one level of xml elements.
             */
            else {
                ArrayList<Node> nodes = new ArrayList<Node>();
                String translatableString = "";
                boolean removeWhitespaceFromStart = true;

                final int childrenLength = children.getLength();
                for (int i = 0; i < childrenLength; ++i) {
                    final Node child = children.item(i);
                    final String childNodeName = child.getNodeName();

                    // does this child have another level of translatable tags?
                    final boolean containsTranslatableTags = doesElementContainTranslatableContentV3(child);
                    final boolean childTranslatableElement = TRANSLATABLE_ELEMENTS.contains(childNodeName);
                    final boolean childInlineElement = INLINE_ELEMENTS.contains(childNodeName);

                    // if so, save the string we have been building up, process the child, and start building up a new string
                    if ((containsTranslatableTags || childTranslatableElement) && !childInlineElement) {
                        if (nodes.size() != 0) {
                            /*
                             * We have found a child node that itself contains some translatable children. In this case we
                             * create a new translatable string. It is possible that the translatableString has some
                             * insignificant trailing whitespace, because the call to the cleanTranslationText function in the
                             * else statement below has assumed that the node being processed was not the last one in the
                             * translatable string, making the trailing whitespace important. So we clean up the trailing
                             * whitespace here.
                             */

                            final Matcher matcher = TRAILING_WHITESPACE_RE_PATTERN.matcher(translatableString);
                            if (matcher.matches()) translatableString = matcher.group("content");

                            addTranslationToNodeDetailsToCollection(translatableString, nodes, allowDuplicates, translationStrings);

                            translatableString = "";
                            nodes = new ArrayList<Node>();
                            removeWhitespaceFromStart = true;
                        }

                        getTranslatableStringsFromNodeV3(child, translationStrings, allowDuplicates, xmlProperties);
                    } else {
                        final String childName = child.getNodeName();
                        final String childText = XMLUtilities.convertNodeToString(child, true);

                        final boolean isVerbatimNode = xmlProperties.isVerbatim() || VERBATIM_ELEMENTS.contains(childName);
                        final String thisTranslatableString;
                        if (isVerbatimNode) {
                            thisTranslatableString = childText;
                        } else {
                            thisTranslatableString = cleanTranslationText(childText, removeWhitespaceFromStart, i == childrenLength - 1);
                        }

                        if (isVerbatimNode || !thisTranslatableString.isEmpty()) {
                            if (!isVerbatimNode && thisTranslatableString.matches("^\\s+$")) {
                                // Pure whitespace nodes should be collapsed down to a single space, unless it is the start or end
                                if (!(i == 0 || i == childrenLength - 1)) {
                                    translatableString += " ";
                                    removeWhitespaceFromStart = false;
                                }
                            } else {
                                translatableString += thisTranslatableString;
                                removeWhitespaceFromStart = false;
                            }
                        }

                        nodes.add(child);
                    }
                }

                // save the last translated string
                if (nodes.size() != 0) {
                    addTranslationToNodeDetailsToCollection(translatableString, nodes, allowDuplicates, translationStrings);

                    translatableString = "";
                }
            }
        } else {
            // if we hit a non-translatable element, process its children
            final NodeList nodeList = node.getChildNodes();
            for (int i = 0; i < nodeList.getLength(); ++i) {
                final Node child = nodeList.item(i);
                getTranslatableStringsFromNodeV3(child, translationStrings, allowDuplicates, xmlProperties);
            }
        }
    }

    public static void replaceTranslatedStrings(final Document xml, final Map<String, String> translations,
            final List<StringToNodeCollection> stringToNodeCollections) {
        if (xml == null || translations == null || translations.size() == 0 || stringToNodeCollections == null || stringToNodeCollections
                .size() == 0)
            return;

        final StringBuilder globalNamespaces = new StringBuilder();
        final NamedNodeMap attrs = xml.getDocumentElement().getAttributes();
        for (int i = 0; i < attrs.getLength(); i++) {
            final Attr attr = (Attr) attrs.item(i);
            if (attr.getName().startsWith("xmlns")) {
                globalNamespaces.append(" ");
                globalNamespaces.append(attr.getNodeName()).append("=\"").append(attr.getNodeValue()).append("\"");
            }
        }

        /*
         * We assume that the xml being provided here is either an exact match, or modified by Zanata in some predictable way
         * (i.e. some padding removed), as supplied to the getTranslatableStrings originally, which we then assume matches the
         * strings supplied as the keys in the translations parameter.
         */

        if (stringToNodeCollections == null || stringToNodeCollections.size() == 0) return;

        for (final StringToNodeCollection stringToNodeCollection : stringToNodeCollections) {
            final String originalString = stringToNodeCollection.getTranslationString();
            final ArrayList<ArrayList<Node>> nodeCollections = stringToNodeCollection.getNodeCollections();

            if (nodeCollections != null && nodeCollections.size() != 0) {
                // Zanata will remove any leading/trailing whitespace due to XML serialization. Here we account for any trimming that was
                // done.
                final TranslatedStringDetails fixedStringDetails = new TranslatedStringDetails(translations, originalString);

                if (fixedStringDetails.getFixedString() != null) {
                    final String translation = translations.get(fixedStringDetails.getFixedString());

                    if (translation != null && !translation.isEmpty()) {
                        // Build up the padding that Zanata removed
                        final StringBuilder leftTrimPadding = new StringBuilder();
                        final StringBuilder rightTrimPadding = new StringBuilder();

                        for (int i = 0; i < fixedStringDetails.getLeftTrimCount(); ++i)
                            leftTrimPadding.append(" ");

                        for (int i = 0; i < fixedStringDetails.getRightTrimCount(); ++i)
                            rightTrimPadding.append(" ");

                        // wrap the returned translation in a root element
                        final String wrappedTranslation = "<tempRoot" + globalNamespaces.toString() + ">" + leftTrimPadding + translation
                                + rightTrimPadding + "</tempRoot>";

                        // convert the wrapped translation into an XML document
                        Document translationDocument = null;
                        try {
                            translationDocument = XMLUtilities.convertStringToDocument(wrappedTranslation);
                        } catch (Exception ex) {
                            LOG.error("Unable to convert Translated String to a DOM Document", ex);
                        }

                        // was the conversion successful
                        if (translationDocument != null) {
                            for (final ArrayList<Node> nodes : nodeCollections) {
                                if (nodes != null && nodes.size() != 0) {
                                    // All nodes in a collection should share the same parent
                                    final Node parent = nodes.get(0).getParentNode();

                                    if (parent != null) {
                                        /*
                                         * Replace the old node with contents of the new node. To do this we need to iterate
                                         * over the children and place them from last to first after the node. This will ensure
                                         * the order of the nodes is kept. Also note that we can't just insert into the parent
                                         * at the start or end as there maybe more refined translations (ie an itemizedList) in
                                         * the middle of the content.
                                         */
                                        final Node importNode = xml.importNode(translationDocument.getDocumentElement(), true);
                                        final NodeList translatedChildren = importNode.getChildNodes();
                                        for (int i = translatedChildren.getLength() - 1; i >= 0; i--) {
                                            if (nodes.get(0).getNextSibling() == null) {
                                                parent.appendChild(translatedChildren.item(i));
                                            } else {
                                                parent.insertBefore(translatedChildren.item(i), nodes.get(0).getNextSibling());
                                            }
                                        }

                                        // remove the original node that the translated text came from
                                        for (final Node node : nodes) {
                                            if (parent == node.getParentNode()) parent.removeChild(node);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private static StringToNodeCollection findExistingText(final String text, final List<StringToNodeCollection> translationStrings) {
        for (final StringToNodeCollection stringToNodeCollection : translationStrings) {
            if (stringToNodeCollection.getTranslationString().equals(text)) return stringToNodeCollection;
        }

        return null;
    }

    private static void addTranslationToNodeDetailsToCollection(final String text, final Node node, final boolean allowDuplicates,
            final List<StringToNodeCollection> translationStrings) {
        final ArrayList<Node> nodes = new ArrayList<Node>();
        nodes.add(node);
        addTranslationToNodeDetailsToCollection(text, nodes, allowDuplicates, translationStrings);
    }

    private static void addTranslationToNodeDetailsToCollection(final String text, final ArrayList<Node> nodes,
            final boolean allowDuplicates, final List<StringToNodeCollection> translationStrings) {
        // Ignore blank or empty string
        if (text == null || text.length() == 0) {
            return;
        }

        if (allowDuplicates) {
            translationStrings.add(new StringToNodeCollection(text).addNodeCollection(nodes));
        } else {
            final StringToNodeCollection stringToNodeCollection = findExistingText(text, translationStrings);

            if (stringToNodeCollection == null) translationStrings.add(new StringToNodeCollection(text).addNodeCollection(nodes));
            else stringToNodeCollection.addNodeCollection(nodes);
        }
    }

    /**
     * Cleans a string for presentation to a translator
     */
    private static String cleanTranslationText(final String input, final boolean removeWhitespaceFromStart,
            final boolean removeWhitespaceFromEnd) {
        String retValue = XMLUtilities.cleanText(input);

        retValue = retValue.trim();

        /*
         * When presenting the contents of a childless XML node to the translator, there is no need for white space padding.
         * When building up a translatable string from a succession of text nodes, whitespace becomes important.
         */
        if (!removeWhitespaceFromStart) {
            if (PRECEEDING_WHITESPACE_SIMPLE_RE_PATTERN.matcher(input).matches()) {
                retValue = " " + retValue;
            }
        }

        if (!removeWhitespaceFromEnd) {
            if (TRAILING_WHITESPACE_SIMPLE_RE_PATTERN.matcher(input).matches()) {
                retValue += " ";
            }
        }

        return retValue;
    }

    /**
     * Wraps the xml if required so that validation can be performed. An example of where this is required is if you are validating
     * against Abstracts, Author Groups or Legal Notices for DocBook 5.0.
     *
     * @param docBookVersion The DocBook version the document will be validated against.
     * @param xml            The xml that needs to be validated.
     * @return A {@link Pair} containing the root element name and the wrapped xml content.
     */
    public static Pair<String, String> wrapForValidation(final DocBookVersion docBookVersion, final String xml) {
        final String rootEleName = XMLUtilities.findRootElementName(xml);
        if (docBookVersion == DocBookVersion.DOCBOOK_50) {
            if (rootEleName.equals("abstract") || rootEleName.equals("legalnotice") || rootEleName.equals("authorgroup")) {
                final String preamble = XMLUtilities.findPreamble(xml);

                final StringBuilder buffer = new StringBuilder("<book><info><title />");
                if (preamble != null) {
                    buffer.append(xml.replace(preamble, ""));
                } else {
                    buffer.append(xml);
                }
                buffer.append("</info></book>");

                return new Pair<String, String>("book", DocBookUtilities.addDocBook50Namespace(buffer.toString()));
            } else if (rootEleName.equals("info")) {
                final String preamble = XMLUtilities.findPreamble(xml);

                final StringBuilder buffer = new StringBuilder("<book>");
                if (preamble != null) {
                    buffer.append(xml.replace(preamble, ""));
                } else {
                    buffer.append(xml);
                }
                buffer.append("</book>");

                return new Pair<String, String>("book", DocBookUtilities.addDocBook50Namespace(buffer.toString()));
            }
        }

        return new Pair<String, String>(rootEleName, xml);
    }

    /**
     * Some docbook elements need to be wrapped up so they can be properly transformed by the docbook XSL.
     *
     * @param xmlDoc
     */
    public static void wrapForRendering(final Document xmlDoc) {
        // Some topics need to be wrapped up to be rendered properly
        final String documentElementNodeName = xmlDoc.getDocumentElement().getNodeName();
        if (documentElementNodeName.equals("authorgroup") || documentElementNodeName.equals("legalnotice")) {
            final Element currentChild = xmlDoc.createElement(documentElementNodeName);

            xmlDoc.renameNode(xmlDoc.getDocumentElement(), xmlDoc.getNamespaceURI(), "book");
            final Element bookInfo = xmlDoc.createElement("bookinfo");
            xmlDoc.getDocumentElement().appendChild(bookInfo);
            bookInfo.appendChild(currentChild);

            final NodeList existingChildren = xmlDoc.getDocumentElement().getChildNodes();
            for (int childIndex = 0; childIndex < existingChildren.getLength(); ++childIndex) {
                final Node child = existingChildren.item(childIndex);
                if (child != bookInfo) {
                    currentChild.appendChild(child);
                }
            }
        }
    }

    /**
     * Checks to see if the Rows, in XML Tables exceed the maximum number of columns.
     *
     * @param doc The XML DOM Document to be validated.
     * @return True if the XML is valid, otherwise false.
     */
    public static boolean validateTables(final Document doc) {
        final NodeList tables = doc.getElementsByTagName("table");
        for (int i = 0; i < tables.getLength(); i++) {
            final Element table = (Element) tables.item(i);
            if (!validateTableRows(table)) {
                return false;
            }
        }

        final NodeList informalTables = doc.getElementsByTagName("informaltable");
        for (int i = 0; i < informalTables.getLength(); i++) {
            final Element informalTable = (Element) informalTables.item(i);
            if (!validateTableRows(informalTable)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Checks that the
     *
     * @param doc
     * @return
     */
    public static boolean checkForInvalidInfoElements(final Document doc) {
        final List<Node> invalidElements = XMLUtilities.getDirectChildNodes(doc.getDocumentElement(), "title", "subtitle", "titleabbrev");
        return invalidElements != null && !invalidElements.isEmpty();
    }

    /**
     * Validates a Revision History XML DOM Document to ensure that the content is valid for use with Publican.
     *
     * @param doc         The DOM Document that represents the XML that is to be validated.
     * @param dateFormats The valid date formats that can be used.
     * @return Null if there weren't any errors otherwise an error message that states what is wrong.
     */
    public static String validateRevisionHistory(final Document doc, final String[] dateFormats) {
        final List<String> invalidRevNumbers = new ArrayList<String>();

        // Find each <revnumber> element and make sure it matches the publican regex
        final NodeList revisions = doc.getElementsByTagName("revision");
        Date previousDate = null;
        for (int i = 0; i < revisions.getLength(); i++) {
            final Element revision = (Element) revisions.item(i);
            final NodeList revnumbers = revision.getElementsByTagName("revnumber");
            final Element revnumber = revnumbers.getLength() == 1 ? (Element) revnumbers.item(0) : null;
            final NodeList dates = revision.getElementsByTagName("date");
            final Element date = dates.getLength() == 1 ? (Element) dates.item(0) : null;

            // Make sure the rev number is valid and the order is correct
            if (revnumber != null && !revnumber.getTextContent().matches("^([0-9.]*)-([0-9.]*)$")) {
                invalidRevNumbers.add(revnumber.getTextContent());
            } else if (revnumber == null) {
                return "Invalid revision, missing &lt;revnumber&gt; element.";
            }

            // Check the dates are in chronological order
            if (date != null) {
                try {
                    final Date revisionDate = DateUtils.parseDateStrictly(cleanDate(date.getTextContent()), Locale.ENGLISH, dateFormats);
                    if (previousDate != null && revisionDate.after(previousDate)) {
                        return "The revisions in the Revision History are not in descending chronological order, " +
                                "starting from \"" + date.getTextContent() + "\".";
                    }

                    previousDate = revisionDate;
                } catch (Exception e) {
                    // Check that it is an invalid format or just an incorrect date (ie the day doesn't match)
                    try {
                        DateUtils.parseDate(cleanDate(date.getTextContent()), Locale.ENGLISH, dateFormats);
                        return "Invalid revision, the name of the day specified in \"" + date.getTextContent() + "\" doesn't match the " +
                                "date.";
                    } catch (Exception ex) {
                        return "Invalid revision, the date \"" + date.getTextContent() + "\" is not in a valid format.";
                    }
                }
            } else {
                return "Invalid revision, missing &lt;date&gt; element.";
            }
        }

        if (!invalidRevNumbers.isEmpty()) {
            return "Revision History has invalid &lt;revnumber&gt; values: " + CollectionUtilities.toSeperatedString(invalidRevNumbers,
                    ", ") + ". The revnumber must match \"^([0-9.]*)-([0-9.]*)$\" to be valid.";
        } else {
            return null;
        }
    }

    /**
     * Basic method to clean a date string to fix any partial day names. It currently cleans "Thur", "Thurs" and "Tues".
     *
     * @param dateString
     * @return
     */
    private static String cleanDate(final String dateString) {
        if (dateString == null) {
            return dateString;
        }

        String retValue = dateString;
        retValue = THURSDAY_DATE_RE.matcher(retValue).replaceAll("Thu");
        retValue = TUESDAY_DATE_RE.matcher(retValue).replaceAll("Tue");

        return retValue;
    }

    /**
     * Pushing to Zanata will modify strings sent to it for translation due the to XML serialization. This class contains the info
     * necessary to take a string from Zanata and match it to the source XML.
     */
    protected static class TranslatedStringDetails {
        /**
         * The number of spaces that Zanata removed from the left
         */
        private final int leftTrimCount;
        /**
         * The number of spaces that Zanata removed from the right
         */
        private final int rightTrimCount;
        /**
         * The string that was matched to the one returned by Zanata. This will be null if there was no match.
         */
        private final String fixedString;

        TranslatedStringDetails(final Map<String, String> translations, final String originalString) {
        /*
         * Here we account for any trimming that is done by Zanata.
         */
            final String lTrimString = StringUtilities.ltrim(originalString);
            final String rTrimString = StringUtilities.rtrim(originalString);
            final String trimString = originalString.trim();

            final boolean containsExactMatch = translations.containsKey(originalString);
            final boolean lTrimMatch = translations.containsKey(lTrimString);
            final boolean rTrimMatch = translations.containsKey(rTrimString);
            final boolean trimMatch = translations.containsKey(trimString);

        /* remember the details of the trimming, so we can add the padding back */
            if (containsExactMatch) {
                leftTrimCount = 0;
                rightTrimCount = 0;
                fixedString = originalString;
            } else if (lTrimMatch) {
                leftTrimCount = originalString.length() - lTrimString.length();
                rightTrimCount = 0;
                fixedString = lTrimString;
            } else if (rTrimMatch) {
                leftTrimCount = 0;
                rightTrimCount = originalString.length() - rTrimString.length();
                fixedString = rTrimString;
            } else if (trimMatch) {
                leftTrimCount = StringUtilities.ltrimCount(originalString);
                rightTrimCount = StringUtilities.rtrimCount(originalString);
                fixedString = trimString;
            } else {
                leftTrimCount = 0;
                rightTrimCount = 0;
                fixedString = null;
            }
        }

        public int getLeftTrimCount() {
            return leftTrimCount;
        }

        public int getRightTrimCount() {
            return rightTrimCount;
        }

        public String getFixedString() {
            return fixedString;
        }
    }

    public static class XMLProperties {
        private boolean verbatim = false;
        private boolean inline = false;

        public XMLProperties() {

        }

        public XMLProperties(final XMLProperties props) {
            if (props != null) {
                this.inline = props.isInline();
                this.verbatim = props.isVerbatim();
            }
        }

        public boolean isVerbatim() {
            return verbatim;
        }

        public void setVerbatim(boolean verbatim) {
            this.verbatim = verbatim;
        }

        public boolean isInline() {
            return inline;
        }

        public void setInline(boolean inline) {
            this.inline = inline;
        }
    }
}
