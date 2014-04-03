package org.jboss.pressgang.ccms.utils.common;

import static com.google.common.base.Strings.isNullOrEmpty;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import com.google.code.regexp.Matcher;
import com.google.code.regexp.Pattern;
import org.jboss.pressgang.ccms.utils.constants.CommonConstants;
import org.jboss.pressgang.ccms.utils.sort.EntitySubstitutionBoundaryDataBoundaryStartSort;
import org.jboss.pressgang.ccms.utils.structures.EntitySubstitutionBoundaryData;
import org.jboss.pressgang.ccms.utils.structures.InjectionError;
import org.jboss.pressgang.ccms.utils.structures.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Entity;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSSerializer;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * A collection of XML related functions. Note to self: See http://www.gnu.org/s/
 * classpathx/jaxp/apidoc/gnu/xml/dom/ls/DomLSSerializer.html for LSSerializer options
 */
public class XMLUtilities {
    // TODO Need to look at ways of dealing with the range #x10000-#xEFFFF
    public static final String NAME_START_CHAR = ":A-Z_a-z\u00C0-\u00D6\u00D8-\u00F6\u00F8-\u02FF\u0370-\u037D\u037F-\u1FFF\u0200C-\u200D\u2070-\u218F\u2C00-\u2FEF\u3001-\uD7FF\uF900-\uFDCF\uFDF0-\uFFFD";
    public static final String NAME_END_CHAR = NAME_START_CHAR + ".0-9\u00B7\u0300-\u036F\u203F-\u2040-";

    private static final Logger LOG = LoggerFactory.getLogger(XMLUtilities.class);
    private static final String DOCTYPE_NAMED_GROUP = "Doctype";
    private static final Pattern DOCTYPE_PATTERN = Pattern.compile(
            "^(\\s*<\\?xml.*?\\?>)?\\s*(?<" + DOCTYPE_NAMED_GROUP + "><\\!DOCTYPE\\s+.*?(\\[.*\\]\\s*)?>)",
            java.util.regex.Pattern.DOTALL);
    private static final String PREAMBLE_NAMED_GROUP = "Preamble";
    private static final Pattern PREAMBLE_PATTERN = Pattern.compile("^\\s*(?<" + PREAMBLE_NAMED_GROUP + "><\\?xml.*?\\?>)",
            java.util.regex.Pattern.DOTALL);
    private static final String ROOT_ELE_NAMED_GROUP = "Doctype";
    private static final Pattern ROOT_ELE_PATTERN = Pattern.compile("^\\s*<\\s*(?<" + ROOT_ELE_NAMED_GROUP + ">[" + NAME_START_CHAR +
            "][" + NAME_END_CHAR + "]*).*?>");

    public static final String ENCODING_START = "encoding=\"";
    public static final String START_CDATA = "<![CDATA[";
    public static final String END_CDATA_RE = "\\]\\]>";
    public static final String END_CDATA_REPLACE = "]]&gt;";
    public static final String XML_ENTITY_NAMED_GROUP = "name";
    public static final String XML_ENTITY_RE = "\\&(?<" + XML_ENTITY_NAMED_GROUP + ">\\S+?);";
    public static final String DOCTYPE_START = "<!DOCTYPE";
    public static final String DOCTYPE_END = ">";
    public static final String ENTITY_START = "<!ENTITY";
    public static final String ENTITY_END = ">";
    public static final String PREAMBLE_START = "<?xml";
    public static final String PREAMBLE_END = ">";
    public static final String TRAILING_WHITESPACE_RE = "^(?<content>.*?)\\s+$";
    public static final String TRAILING_WHITESPACE_SIMPLE_RE = ".*?\\s+$";
    public static final String PRECEEDING_WHITESPACE_SIMPLE_RE = "^\\s+.*";

    public static final Pattern XML_ENTITY_PATTERN = Pattern.compile("\\&(?<" + XML_ENTITY_NAMED_GROUP + ">\\S+?);");

    public static final Pattern TRAILING_WHITESPACE_RE_PATTERN = Pattern.compile(TRAILING_WHITESPACE_RE,
            java.util.regex.Pattern.MULTILINE | java.util.regex.Pattern.DOTALL);
    public static final Pattern TRAILING_WHITESPACE_SIMPLE_RE_PATTERN = Pattern.compile(TRAILING_WHITESPACE_SIMPLE_RE,
            java.util.regex.Pattern.MULTILINE | java.util.regex.Pattern.DOTALL);
    public static final Pattern PRECEEDING_WHITESPACE_SIMPLE_RE_PATTERN = Pattern.compile(PRECEEDING_WHITESPACE_SIMPLE_RE,
            java.util.regex.Pattern.MULTILINE | java.util.regex.Pattern.DOTALL);

    /**
     * A regular expression that identifies a topic id
     */
    private static final String INJECT_ID_RE_STRING = "(\\d+|T(\\d+|(\\-[ ]*[A-Za-z][A-Za-z\\d\\-_]*)))";
    private static final Pattern INJECT_RE = Pattern.compile(
            "^\\s*(?<TYPE>Inject\\w*)(?<COLON>:?)\\s*(?<IDS>" + INJECT_ID_RE_STRING + ".*)\\s*$",
            java.util.regex.Pattern.CASE_INSENSITIVE);
    private static final Pattern INJECT_ID_RE = Pattern.compile("^[\\d ,]+$");
    private static final Pattern INJECT_SINGLE_ID_RE = Pattern.compile("^[\\d]+$");
    private static final List<String> VALID_INJECTION_TYPES = Arrays.asList("Inject", "InjectList", "InjectListItems",
            "InjectListAlphaSort", "InjectSequence");

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

    public static boolean allEntitiesAccountedFor(final String xml, final Integer format, final List<String> entities) {
        final Pattern pattern = Pattern.compile("&(.*?);");
        final Matcher matcher = pattern.matcher(xml);
        while (matcher.find()) {
            final String entity = matcher.group(1);
            if (entities == null || entities.indexOf(entity) == -1) {
                if ((format == CommonConstants.DOCBOOK_50 || format == CommonConstants.DOCBOOK_45) && !DOCBOOK_ENTITIES.containsKey(entity)) {
                    return false;
                } else {
                    return false;
                }
            }
        }

        return true;
    }

    public static boolean allEntitiesAccountedFor(final String xml, final Integer format, final String entities) {
        final Pattern pattern = Pattern.compile("&(.*?);");
        final Matcher matcher = pattern.matcher(xml);
        while (matcher.find()) {
            final String entity = matcher.group(1);
            final Pattern entityPattern = Pattern.compile("<!ENTITY\\s+" + entity + "\\s+");
            if (entities == null || !entityPattern.matcher(entities).find()) {
                if ((format == CommonConstants.DOCBOOK_50 || format == CommonConstants.DOCBOOK_45) && !DOCBOOK_ENTITIES.containsKey(entity)) {
                    return false;
                } else {
                    return false;
                }
            }
        }

        return true;
    }

    public static String replaceStandardEntities(final Integer format, final String xml) {
        if (format == CommonConstants.DOCBOOK_45 || format == CommonConstants.DOCBOOK_50) {
            return replaceEntities(DOCBOOK_ENTITIES, xml);
        }

        return xml;
    }

    public static String findEncoding(final String xml) {
        // Find the preamble first so we can dissect it to find the encoding.
        final String preamble = findPreamble(xml);
        if (preamble != null) {
            final int encodingIndexStart = preamble.indexOf(ENCODING_START);
            final int firstLineBreak = preamble.indexOf("\n");

            // make sure we found the encoding attribute
            if (encodingIndexStart != -1) {
                final int encodingIndexEnd = preamble.indexOf("\"", encodingIndexStart + ENCODING_START.length());

                // make sure the encoding attribute was found before the first
                // line break
                if (firstLineBreak == -1 || encodingIndexStart < firstLineBreak) {
                    // make sure we found the end of the attribute
                    if (encodingIndexEnd != -1) {
                        return preamble.substring(encodingIndexStart + ENCODING_START.length(), encodingIndexEnd);
                    }
                }
            }
        }

        return null;
    }

    public static String findDocumentType(final String xml) {
        final Matcher matcher = DOCTYPE_PATTERN.matcher(xml);
        if (matcher.find()) {
            return matcher.group(DOCTYPE_NAMED_GROUP);
        } else {
            return null;
        }
    }

    public static String findPreamble(final String xml) {
        final Matcher matcher = PREAMBLE_PATTERN.matcher(xml);
        if (matcher.find()) {
            return matcher.group(PREAMBLE_NAMED_GROUP);
        } else {
            return null;
        }
    }

    public static String findRootElementName(final String xml) {
        String cleanedXML = xml;

        // Remove the preamble
        final Matcher preambleMatcher = PREAMBLE_PATTERN.matcher(cleanedXML);
        if (preambleMatcher.find()) {
            cleanedXML = preambleMatcher.replaceFirst("");
        }

        // Remove the doctype
        final Matcher doctypeMatcher = DOCTYPE_PATTERN.matcher(cleanedXML);
        if (doctypeMatcher.find()) {
            cleanedXML = doctypeMatcher.replaceFirst("");
        }

        final Matcher rootEleMatcher = ROOT_ELE_PATTERN.matcher(cleanedXML);
        if (rootEleMatcher.find()) {
            return rootEleMatcher.group(ROOT_ELE_NAMED_GROUP);
        } else {
            return null;
        }
    }

    /**
     * Removes all of the child nodes from a parent node.
     */
    public static void emptyNode(final Node parent) {
        final NodeList childNodes = parent.getChildNodes();
        for (int i = childNodes.getLength() - 1; i >= 0; i--) {
            final Node childNode = childNodes.item(i);
            childNode.getParentNode().removeChild(childNode);
        }
    }

    /**
     * Clones a document object.
     *
     * @param doc The document to be cloned.
     * @return The new document object that contains the same data as the original document.
     * @throws TransformerException Thrown if the document can't be
     */
    public static Document cloneDocument(final Document doc) throws TransformerException {
        final Node rootNode = doc.getDocumentElement();

        // Copy the doctype and xml version type data
        final TransformerFactory tfactory = TransformerFactory.newInstance();
        final Transformer tx = tfactory.newTransformer();
        final DOMSource source = new DOMSource(doc);
        final DOMResult result = new DOMResult();
        tx.transform(source, result);

        // Copy the actual content into the new document
        final Document copy = (Document) result.getNode();
        copy.removeChild(copy.getDocumentElement());
        final Node copyRootNode = copy.importNode(rootNode, true);
        copy.appendChild(copyRootNode);

        return copy;
    }

    /**
     * This function will return a map that contains entity names as keys, and random integer strings as values. The values are
     * guaranteed not to have appeared in the original xml.
     *
     * @param xml The xml to generate the replacements for
     * @return a map of entity names to unique random strings
     */
    public static Map<String, String> calculateEntityReplacements(final String xml) {
        final Map<String, String> retValue = new HashMap<String, String>();

        final Random randomGenerator = new Random();

        /* find any matches */
        final Matcher injectionSequencematcher = XML_ENTITY_PATTERN.matcher(xml);

        /* loop over the regular expression matches */
        while (injectionSequencematcher.find()) {
            final String entityName = injectionSequencematcher.group(XML_ENTITY_NAMED_GROUP);

            if (!retValue.containsKey(entityName)) {
                String randomReplacement;
                do {
                    randomReplacement = "[" + randomGenerator.nextInt() + "]";
                } while (xml.indexOf(randomReplacement) != -1);

                retValue.put(entityName, randomReplacement);
            }
        }

        return retValue;
    }

    /**
     * This function takes the Map generated by the calculateEntityReplacements function, and uses those values to replace any
     * entities in the XML string with their unique random integer replacements. The end results is an XML string that contains
     * no entities, but contains identifiable strings that can be used to replace those entities at a later point.
     *
     * @param replacements The Map generated by the calculateEntityReplacements function
     * @param xml          The XML string to modify
     * @return The modified XML
     */
    public static String replaceEntities(final Map<String, String> replacements, final String xml) {
        String retValue = xml;
        for (final Entry<String, String> entry : replacements.entrySet())
            retValue = retValue.replaceAll("\\&" + entry.getKey() + ";", entry.getValue());
        return retValue;
    }

    /**
     * This function takes a parsed Document, along with the Map generated by the calculateEntityReplacements function, and
     * restores all the entities.
     *
     * @param replacements The Map generated by the calculateEntityReplacements function
     * @param node         The node to modify
     */
    private static void restoreEntities(final Map<String, String> replacements, final Node node) {
        if (node == null || replacements == null || replacements.size() == 0) return;

        /* make the substitutions for all children nodes */
        final NodeList nodeList = node.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++)
            restoreEntities(replacements, nodeList.item(i));

        /* make the substitutions for all attributes */
        final NamedNodeMap attrList = node.getAttributes();
        if (attrList != null) {
            for (int i = 0; i < attrList.getLength(); i++)
                restoreEntities(replacements, attrList.item(i));
        }

        /* cdata sections just use a straight text replace */
        if (node.getNodeType() == Node.CDATA_SECTION_NODE || node.getNodeType() == Node.COMMENT_NODE) {
            for (final Entry<String, String> entityReplacement : replacements.entrySet()) {
                final String entity = "&" + entityReplacement.getKey() + ";";
                final String markerAsRE = entityReplacement.getValue().replace("[", "\\[").replace("]", "\\]");
                final String textContent = node.getTextContent();
                final String fixedTextContent = textContent.replaceAll(markerAsRE, entity);
                node.setTextContent(fixedTextContent);
            }
        } else if (node.getNodeType() == Node.TEXT_NODE) {
            /* The list of substitution string boundaries */
            final List<EntitySubstitutionBoundaryData> boundaries = new ArrayList<EntitySubstitutionBoundaryData>();

            /*
             * find the start and end indexes of all the substitutions in this text node
             */
            for (final Entry<String, String> entityReplacement : replacements.entrySet()) {
                final String entityName = entityReplacement.getKey();
                final String entityPlaceholder = entityReplacement.getValue();

                /* The length of the placeholder string */
                final int entityPlaceholderLength = entityPlaceholder.length();
                /* The text in this node, with the substitutions */
                final String originalText = node.getTextContent();

                int startIndex = 0;
                while ((startIndex = originalText.indexOf(entityPlaceholder, startIndex)) != -1) {
                    boundaries.add(new EntitySubstitutionBoundaryData(entityName, entityPlaceholder,
                            new Pair<Integer, Integer>(startIndex, startIndex + entityPlaceholderLength - 1)));
                    startIndex += entityPlaceholderLength;
                }
            }

            /*
             * if there are no boundaries, there is no need to do any substitutions
             */
            if (boundaries.size() != 0) {
                /* Sort based on the start of the boundaries */
                Collections.sort(boundaries, new EntitySubstitutionBoundaryDataBoundaryStartSort());

                /* get the text content of the text node */
                final String originalText = node.getTextContent();

                /* the parent of this node holds only this text node. */
                final Node parentNode = node.getParentNode();

                /*
                 * loop through all the boundaries that define the position of the substitutions, and replace them with entity
                 * reference nodes.
                 * 
                 * this involves adding a new sequence of text and entity reference nodes before the existing text node, and
                 * then removing the existing text node.
                 */
                for (int i = 0; i < boundaries.size(); ++i) {
                    final EntitySubstitutionBoundaryData boundary = boundaries.get(i);
                    final EntitySubstitutionBoundaryData lastBoundary = i != 0 ? boundaries.get(i - 1) : null;

                    /*
                     * The entity reference node.
                     * 
                     * Normal Elements can contain EntityReference nodes, however attributes appear to be unable to handle
                     * EntityReference nodes as children, so just convert the EntityReference to a normal text node in the
                     * Attribute.
                     */
                    final Node entityNode;
                    if (parentNode.getNodeType() == Node.ATTRIBUTE_NODE) {
                        entityNode = parentNode.getOwnerDocument().createTextNode("&" + boundary.getEntityName() + ";");
                    } else {
                        entityNode = parentNode.getOwnerDocument().createEntityReference(boundary.getEntityName());
                    }

                    /* the first substitution where text proceeds it */
                    if (i == 0) {
                        if (boundary.getBoundary().getFirst() != 0) {
                            final Node textNode = parentNode.getOwnerDocument().createTextNode(
                                    originalText.substring(0, boundary.getBoundary().getFirst()));
                            parentNode.insertBefore(textNode, node);
                        }

                        /* append an entity node after the initial text node */
                        parentNode.insertBefore(entityNode, node);
                    } else {
                        /*
                         * there is a gap between the last boundary and this boundary
                         */

                        if (lastBoundary.getBoundary().getSecond() + 1 != boundary.getBoundary().getFirst()) {
                            final Node textNode = parentNode.getOwnerDocument().createTextNode(
                                    originalText.substring(lastBoundary.getBoundary().getSecond() + 1, boundary.getBoundary().getFirst()));
                            parentNode.insertBefore(textNode, node);
                        }
                    }

                    /*
                     * append an entity node after the text node following the last substitution
                     */
                    parentNode.insertBefore(entityNode, node);

                    /* the last substitution where text follows it */
                    if (i == boundaries.size() - 1) {
                        /* append an entity node before the last text node */
                        parentNode.insertBefore(entityNode, node);

                        if (boundary.getBoundary().getSecond() != originalText.length() - 1) {
                            final Node textNode = parentNode.getOwnerDocument().createTextNode(
                                    originalText.substring(boundary.getBoundary().getSecond() + 1));
                            parentNode.insertBefore(textNode, node);
                        }
                    }
                }

                /* finally, remove the existing text node */
                parentNode.removeChild(node);
            }
        }
    }

    /**
     * @param xml The XML to be converted
     * @return A Document converted from the supplied XML, or null if the supplied XML was invalid
     * @throws SAXException
     */
    public static Document convertStringToDocument(final String xml) throws SAXException {
        return convertStringToDocument(xml, true, true);
    }

    /**
     * @param xml The XML to be converted
     * @param preserveEntities Whether or not entities should be renamed prior to converting the string to an xml document.
     *                         This is useful if your xml has entity references that are not defined in the string
     *                         you are converting
     * @return A Document converted from the supplied XML, or null if the supplied XML was invalid
     * @throws SAXException
     */
    public static Document convertStringToDocument(final String xml, final boolean preserveEntities) throws SAXException {
        return convertStringToDocument(xml, preserveEntities, true);
    }

    /**
     * @param xml The XML to be converted
     * @param preserveEntities Whether or not entities should be renamed prior to converting the string to an xml document.
     *                         This is useful if your xml has entity references that are not defined in the string
     *                         you are converting
     * @param restoreEntities  Whether or not you want to renamed the entities converted by setting preserveEntities to true
     *                         back to entity references. This would be set to false if you want to validate the XML when it
     *                         has entity references that are not defined.
     * @return A Document converted from the supplied XML, or null if the supplied XML was invalid
     * @throws SAXException
     */
    public static Document convertStringToDocument(final String xml, final boolean preserveEntities, final boolean restoreEntities) throws SAXException {
        if (xml == null) return null;

        try {
            // find the encoding, defaulting to UTF-8
            String encoding = findEncoding(xml);
            if (encoding == null) encoding = "UTF-16";

            /*
             * Xerces does not seem to have any way of simply importing entities "as is". It will try to expand them, which we
             * don't want. As a work around the calculateEntityReplacements() function will map entity names to random
             * substitution markers. These markers are parsed as plain text (they are in the format "[random_integer]"). The
             * replaceEntities() function will then replace the entity definitions in the source XML text with these
             * substitution markers.
             * 
             * At this point the XML has no entities, and so Xerces will parse the string without trying to expand the entities.
             * 
             * Once we have a Document object, we run the restoreEntities() function, which replaces the substitution markers
             * with entity reference nodes. Xerces does not try to expand entities when serializing a Document object to a
             * string, nor does it try to extend entity reference nodes when they are added. In this way we can parse any XML
             * and retain the entities without having to link to any DTDs or implement any EntityResolvers.
             */
            final Map<String, String> replacements = calculateEntityReplacements(xml);
            final String fixedXML = preserveEntities ? replaceEntities(replacements, xml) : xml;

            final DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
            // This was causing an exception... See below with the EntityResolver for an alternative.
            // builderFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            // this is the default, but set it anyway
            // builderFactory.setValidating(false);
            builderFactory.setNamespaceAware(true);

            final DocumentBuilder builder = builderFactory.newDocumentBuilder();

            // disable the resolution of any entities. see http://stackoverflow.com/a/155330/157605
            builder.setEntityResolver(new EntityResolver() {
                public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
                    // Return an empty source so that File Not Found errors aren't generated.
                    return new InputSource(new ByteArrayInputStream("<?xml version='1.0' encoding='UTF-8'?>".getBytes()));
                }
            });

            // Create an error handler that does nothing, so that the default handler (which only prints to stderr) isn't used.
            builder.setErrorHandler(new ErrorHandler() {
                @Override
                public void warning(SAXParseException e) throws SAXException {
                    // Do nothing
                }

                @Override
                public void error(SAXParseException e) throws SAXException {
                    // Do nothing
                }

                @Override
                public void fatalError(SAXParseException e) throws SAXException {
                    // Do nothing
                }
            });

            // http://www.mkyong.com/java/how-to-read-utf-8-xml-file-in-java-sax-parser/
            final InputSource inputSource = new org.xml.sax.InputSource(new ByteArrayInputStream(fixedXML.getBytes(encoding)));
            inputSource.setEncoding(encoding);
            final Document document = builder.parse(inputSource);

            if (preserveEntities && restoreEntities) {
                restoreEntities(replacements, document.getDocumentElement());
            }

            return document;
        } catch (SAXException ex) {
            throw ex;
        } catch (ParserConfigurationException ex) {
            throw new RuntimeException(ex);
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException(ex);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Removes any child elements that match the type
     * @param parent The element whose children will be removed
     * @param type The name of the children elements to remove
     */
    public static void removeChildrenOfType(final Node parent, final String type) {
        final NodeList children = parent.getChildNodes();
        for (int childIndex = 0; childIndex < children.getLength(); ++childIndex) {
            final Node child = children.item(childIndex);
            if (child.getNodeName().equals("title")) {
                parent.removeChild(child);
                break;
            }
        }
    }

    /**
     * Sets the contents of the child elements that match the type
     * @param parent The element whose children will be updated
     * @param type The name of the children elements to updated
     * @param firstOnly True if only the first child of type is to be updated
     */
    public static void setChildrenContent(final Node parent, final String type, final String content, final boolean firstOnly) {
        final NodeList children = parent.getChildNodes();
        for (int childIndex = 0; childIndex < children.getLength(); ++childIndex) {
            final Node child = children.item(childIndex);
            if (child.getNodeName().equals(type)) {
                child.setTextContent(content);
                if (firstOnly) {
                    break;
                }
            }
        }
    }

    /**
     * Converts a Document to a String
     *
     * @param doc The Document to be converted
     * @return The String representation of the Document
     */
    public static String convertDocumentToString(final Document doc, final String encoding) {
        String retValue = convertDocumentToString(doc);

        /*
         * The encoding used is the encoding of the DOMString type, i.e. UTF-16
         * (http://www.w3.org/TR/DOM-Level-3-LS/load-save.html#LS-LSSerializer- writeToString). However, we need to use UTF-8
         * (https://bugzilla.redhat.com/show_bug.cgi?id=735904). So do a simple text replacement.
         */

        final String docEncoding = findEncoding(retValue);
        if (docEncoding != null) retValue = retValue.replace(docEncoding, encoding);

        return retValue;
    }

    /**
     * Convert an XML document to a string.
     *
     * @param doc       The Document to be converted
     * @param encoding  The encoding of the XML
     * @param entityDec Any additional XML entity declarations
     * @return The String representation of the XML Document
     */
    public static String convertDocumentToString(final Document doc, final String encoding, final String entityDec) {
        String retValue = convertDocumentToString(doc, encoding);

        final String docEncoding = findPreamble(retValue);
        if (docEncoding != null) retValue = retValue.replace(docEncoding, docEncoding + "\n" + entityDec);

        return retValue;
    }

    /**
     * Converts a Document to a String
     *
     * @param doc The Document to be converted
     * @return The String representation of the Document
     */
    public static String convertDocumentToString(final Document doc) {
        return convertDocumentToString(doc, false);
    }

    /**
     * Converts a Document to a String
     *
     * @param doc         The Document to be converted
     * @param prettyPrint If the xml should be formatted when being converted.
     * @return The String representation of the Document
     */
    public static String convertDocumentToString(final Document doc, final boolean prettyPrint) {
        final DOMImplementationLS domImplementation = (DOMImplementationLS) doc.getImplementation();
        final LSSerializer lsSerializer = domImplementation.createLSSerializer();
        if (prettyPrint) {
            lsSerializer.getDomConfig().setParameter("format-pretty-print", Boolean.TRUE);
        }
        if (doc.getXmlEncoding() != null) {
            final LSOutput lsOutput =  domImplementation.createLSOutput();
            lsOutput.setEncoding(doc.getXmlEncoding());
            final Writer stringWriter = new StringWriter();
            lsOutput.setCharacterStream(stringWriter);
            lsSerializer.write(doc, lsOutput);
            return stringWriter.toString();
        } else {
            return lsSerializer.writeToString(doc);
        }
    }

    public static String removePreamble(final String xml) {
        final String preamble = XMLUtilities.findPreamble(xml);
        if (preamble != null) {
            return xml.replace(preamble, "");
        }

        return xml;
    }

    private static void appendIndent(final StringBuffer stringBuffer, final boolean tabIndent, final int indentLevel,
            final int indentCount) {
        final char indent = tabIndent ? '\t' : ' ';

        final int totalIndentCount = indentLevel * indentCount;

        stringBuffer.append("\n");
        for (int i = 0; i < totalIndentCount; ++i)
            stringBuffer.append(indent);
    }

    public static String convertNodeToString(final Node startNode, final boolean includeElementName) {
        return convertNodeToString(startNode, includeElementName, true, false, new ArrayList<String>(), new ArrayList<String>(),
                new ArrayList<String>(), true, 0, 0);
    }

    public static String convertNodeToString(final Node startNode, final List<String> verbatimElements, final List<String> inlineElements,
            final List<String> contentsInlineElements, final boolean tabIndent) {
        return convertNodeToString(startNode, true, false, false, verbatimElements, inlineElements, contentsInlineElements, tabIndent, 1,
                0);
    }

    /**
     * Converts a Node to a String.
     *
     * @param node               The Node to be converted
     * @param includeElementName true if the string should include the name of the node, or false if it is just to include the
     *                           contents of the node
     * @return The String representation of the Node
     */
    public static String convertNodeToString(final Node startNode, final boolean includeElementName, final boolean verbatim,
            final boolean inline, final List<String> verbatimElements, final List<String> inlineElements,
            final List<String> contentsInlineElements, final boolean tabIndent, final int indentCount, final int indentLevel) {
        /* Find out if this node is a document */
        final Node node = startNode instanceof Document ? ((Document) startNode).getDocumentElement() : startNode;

        final String nodeName = node.getNodeName();
        final short nodeType = node.getNodeType();
        final StringBuffer stringBuffer = new StringBuffer();

        /*
         * Find out if the previous node was a comment (excluding any empty text nodes). Also find out if this is the first node
         * in the parent.
         */
        boolean previousNodeWasComment = false;
        Node previousNode = startNode.getPreviousSibling();
        while (previousNode != null) {
            if ((previousNode.getNodeType() == Node.TEXT_NODE && previousNode.getNodeValue().trim().isEmpty())) {
                previousNode = previousNode.getPreviousSibling();
                continue;
            }

            if (previousNode.getNodeType() == Node.COMMENT_NODE) {
                previousNodeWasComment = true;
                break;
            }

            break;
        }

        /* Find out of this node is the document root node */
        final boolean documentRoot = node.getOwnerDocument().getDocumentElement() == node;

        final boolean firstNode = previousNode == null;

        if (Node.CDATA_SECTION_NODE == nodeType) {
            final StringBuffer retValue = new StringBuffer();

            if (!verbatim && !inline) appendIndent(retValue, tabIndent, indentLevel, indentCount);

            if (includeElementName) retValue.append("<![CDATA[");
            retValue.append(node.getNodeValue());
            if (includeElementName) retValue.append("]]>");

            return retValue.toString();
        }

        if (Node.COMMENT_NODE == nodeType) {
            final StringBuffer retValue = new StringBuffer();

            if (!verbatim && !inline) {
                // If the previous node is a text node that isn't just whitespace then the comment must follow on, so don't add an indent
                if (previousNode != null && previousNode instanceof Text) {
                    if (previousNode.getTextContent().trim().isEmpty()) {
                        appendIndent(retValue, tabIndent, indentLevel, indentCount);
                    }
                } else {
                    appendIndent(retValue, tabIndent, indentLevel, indentCount);
                }
            }

            if (includeElementName) retValue.append("<!--");
            retValue.append(node.getNodeValue());
            if (includeElementName) retValue.append("-->");

            return retValue.toString();
        }

        if (Node.TEXT_NODE == nodeType) {
            if (!verbatim) {
                String trimmedNodeValue = cleanText(node.getNodeValue());

                if (!trimmedNodeValue.trim().isEmpty()) {
                    final StringBuffer retValue = new StringBuffer();

                    /*
                     * if this is the first text node, remove all preceeding whitespace, and then add the indent
                     */
                    final boolean firstNotInlinedTextNode = !inline && firstNode;
                    if (firstNotInlinedTextNode) {
                        appendIndent(retValue, tabIndent, indentLevel, indentCount);
                    }

                    // Remove any white space at the beginning and end of the text, save for one space
                    final boolean startedWithWhiteSpace = StringUtilities.startsWithWhitespace(trimmedNodeValue);
                    final boolean endedWithWhitespace = StringUtilities.endsWithWhitespace(trimmedNodeValue);

                    while (StringUtilities.startsWithWhitespace(trimmedNodeValue)) {
                        trimmedNodeValue = trimmedNodeValue.substring(1);
                    }

                    while (StringUtilities.endsWithWhitespace(trimmedNodeValue)) {
                        trimmedNodeValue = trimmedNodeValue.substring(0, trimmedNodeValue.length() - 1);
                    }

                    // Only add whitespace if the node is in an inline element or isn't the first node
                    if (startedWithWhiteSpace && (inline || !firstNode)) trimmedNodeValue = " " + trimmedNodeValue;

                    // Only add whitespace if the node is in an inline element or isn't the last node
                    if (endedWithWhitespace && (node.getNextSibling() != null || inline)) trimmedNodeValue += " ";

                    retValue.append(trimmedNodeValue);

                    return retValue.toString();
                }
                /*
                 * Allow for spaces between nodes. i.e. <literal>Test</literal> <literal>Test2</literal>
                 */
                else {
                    // is this text node only whitespace
                    final boolean thisTextNodeIsWhiteSpace = node.getNodeValue() != null && node.getNodeValue().matches("^\\s+$");
                    // is the next node going to be placed on the same line
                    final boolean thisTextNodeHasInlineSibling =
                            node.getNextSibling() != null &&
                            (
                                inlineElements.contains(node.getNextSibling().getNodeName()) ||
                                node.getNextSibling().getNodeType() != Node.ELEMENT_NODE
                            );
                    // is the parent node closing element going to be placed on the same line
                    final boolean thisTextNodeIsLastInInlineParent =
                            node.getNextSibling() == null &&
                            node.getParentNode() != null &&
                            inlineElements.contains(node.getParentNode().getNodeName());

                    if (thisTextNodeIsWhiteSpace && (thisTextNodeHasInlineSibling || thisTextNodeIsLastInInlineParent)) {
                        return " ";
                    } else {
                        return "";
                    }
                }
            } else {
                return node.getNodeValue();
            }
        }

        if (Node.ENTITY_REFERENCE_NODE == nodeType) {
            final StringBuffer retValue = new StringBuffer();

            // if this is the first node, then add the indent
            if (!inline && !verbatim && firstNode) {
                appendIndent(retValue, tabIndent, indentLevel, indentCount);
            }

            if (includeElementName) retValue.append("&");
            retValue.append(node.getNodeName());
            if (includeElementName) retValue.append(";");

            return retValue.toString();
        }

        if (Node.PROCESSING_INSTRUCTION_NODE == nodeType) {
            final StringBuffer retValue = new StringBuffer();

            // Add the indent
            appendIndent(retValue, tabIndent, indentLevel, indentCount);

            ProcessingInstruction processingInstruction = (ProcessingInstruction) node;
            retValue.append("<?");
            retValue.append(processingInstruction.getTarget()).append(" ");
            retValue.append(processingInstruction.getData());
            retValue.append("?>");

            return retValue.toString();
        }

        /* open the tag */
        if (includeElementName) {

            if  (
                    !verbatim &&
                    !documentRoot &&
                    (
                        (!inline && !inlineElements.contains(nodeName)) ||
                        previousNodeWasComment ||
                        (firstNode && !inline)
                    )
                ) {
                appendIndent(stringBuffer, tabIndent, indentLevel, indentCount);
            }

            stringBuffer.append('<').append(nodeName);

            /* add attributes */
            final NamedNodeMap attrs = node.getAttributes();
            if (attrs != null) {
                for (int i = 0; i < attrs.getLength(); i++) {
                    final Node attr = attrs.item(i);
                    stringBuffer.append(' ').append(attr.getNodeName()).append("=\"").append(attr.getNodeValue()).append("\"");
                }
            }
        }

        /* deal with children */
        final NodeList children = node.getChildNodes();
        if (children.getLength() == 0) {
            final String nodeTextContent = node.getTextContent();
            if (nodeTextContent.length() == 0) {
                if (includeElementName) stringBuffer.append("/>");
            } else {
                stringBuffer.append(nodeTextContent);

                /* indent */
                if (!verbatim && !inline && !inlineElements.contains(nodeName))
                    appendIndent(stringBuffer, tabIndent, indentLevel, indentCount);

                /* close that tag */
                if (includeElementName) stringBuffer.append("</").append(nodeName).append('>');
            }
        } else {
            if (includeElementName) stringBuffer.append(">");

            final boolean inlineMyChildren = inline || inlineElements.contains(nodeName) || contentsInlineElements.contains(nodeName);
            final boolean verbatimMyChildren = verbatim || verbatimElements.contains(nodeName);

            for (int i = 0; i < children.getLength(); ++i) {
                final String childToString = convertNodeToString(children.item(i), true, verbatimMyChildren, inlineMyChildren,
                        verbatimElements, inlineElements, contentsInlineElements, tabIndent, indentCount, indentLevel + 1);
                if (childToString.length() != 0) stringBuffer.append(childToString);
            }

            /* close that tag */
            if (includeElementName) {
                /* indent */
                if (!verbatimMyChildren && !inlineMyChildren) appendIndent(stringBuffer, tabIndent, indentLevel, indentCount);

                stringBuffer.append("</").append(nodeName).append('>');
            }
        }

        return stringBuffer.toString();
    }

    /**
     * Scans a node and all of its children for nodes of a particular type.
     *
     * @param parent    The parent node to search from.
     * @param nodeNames A single node name or list of node names to search for
     * @return A List of all the nodes found matching the nodeName(s) under the parent
     */
    public static List<Node> getChildNodes(final Node parent, final String... nodeNames) {
        return getChildNodes(parent, true, nodeNames);
    }

    public static List<Node> getComments(final Node parent) {
        return getChildNodes(parent, "#comment");
    }

    /**
     * Scans a node for directly related child nodes of a particular type. This method will not scan for nodes that aren't a child of the
     * parent node.
     *
     * @param parent    The parent node to search from.
     * @param nodeNames A single node name or list of node names to search for
     * @return A List of all the nodes found matching the nodeName(s) under the parent
     */
    public static List<Node> getDirectChildNodes(final Node parent, final String... nodeNames) {
        return getChildNodes(parent, false, nodeNames);
    }

    /**
     * Scans a node and all of its children for nodes of a particular type.
     *
     * @param parent          The parent node to search from.
     * @param recursiveSearch If the child nodes should be recursively searched.
     * @param nodeNames       A single node name or list of node names to search for
     * @return a List of all the nodes found matching the nodeName under the parent
     */
    protected static List<Node> getChildNodes(final Node parent, boolean recursiveSearch, final String... nodeNames) {
        final List<Node> nodes = new ArrayList<Node>();
        final NodeList children = parent.getChildNodes();
        for (int i = 0; i < children.getLength(); ++i) {
            final Node child = children.item(i);

            for (final String nodeName : nodeNames) {
                if (child.getNodeName().equals(nodeName)) {
                    nodes.add(child);
                }
                if (recursiveSearch) {
                    nodes.addAll(getChildNodes(child, true, nodeName));
                }
            }
        }
        return nodes;
    }

    /**
     * Add/Set the DOCTYPE for some XML content.
     *
     * @param xml             The XML to add or set the DOCTYPE for.
     * @param rootElementName The root Element Name for the DOCTYPE.
     * @return The XML with the DOCTYPE added.
     */
    public static String addDoctype(final String xml, final String rootElementName) {
        return addDoctype(xml, rootElementName, null);
    }

    /**
     * Add/Set the DOCTYPE for some XML content.
     *
     * @param xml             The XML to add or set the DOCTYPE for.
     * @param rootElementName The root Element Name for the DOCTYPE.
     * @param entityFileName  The file name for any external entities that should be included.
     * @return The XML with the DOCTYPE added.
     */
    public static String addDoctype(final String xml, final String rootElementName, final String entityFileName) {
        final String preamble = findPreamble(xml);
        final String docType = findDocumentType(xml);
        final String fixedPreamble = preamble == null ? "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n" : preamble + "\n";

        // Remove any current doctype declarations
        final String fixedXML;
        if (docType != null) {
            final String tempFixedXML = preamble == null ? xml : xml.replace(preamble, "");
            fixedXML = tempFixedXML.replace(docType, "");
        } else {
            fixedXML = preamble == null ? xml : xml.replace(preamble, "");
        }

        final StringBuilder retValue = new StringBuilder(fixedPreamble);
        retValue.append("<!DOCTYPE ");
        if (rootElementName == null) {
            retValue.append("chapter");
        } else {
            retValue.append(rootElementName);
        }

        // Add the local entity file
        if (entityFileName != null) {
            retValue.append(" [\n");
            retValue.append("<!ENTITY % BOOK_ENTITIES SYSTEM \"" + entityFileName + "\">\n");
            retValue.append("%BOOK_ENTITIES;\n");
            retValue.append("]");
        }

        retValue.append(">\n");
        retValue.append(fixedXML);

        return retValue.toString();
    }

    /**
     * Add/Set the PUBLIC DOCTYPE for some XML content.
     *
     * @param xml             The XML to add or set the DOCTYPE for.
     * @param publicName      The PUBLIC name for the DOCTYPE.
     * @param publicLocation  The PUBLIC location/url for the DOCTYPE.
     * @param rootElementName The root Element Name for the DOCTYPE.
     * @return The XML with the DOCTYPE added.
     */
    public static String addPublicDoctype(final String xml, final String publicName, final String publicLocation,
            final String rootElementName) {
        return addPublicDoctype(xml, publicName, publicLocation, null, rootElementName);
    }

    /**
     * Add/Set the PUBLIC DOCTYPE for some XML content.
     *
     * @param xml             The XML to add or set the DOCTYPE for.
     * @param publicName      The PUBLIC name for the DOCTYPE.
     * @param publicLocation  The PUBLIC location/url for the DOCTYPE.
     * @param entityFileName  The file name for any external entities that should be included.
     * @param rootElementName The root Element Name for the DOCTYPE.
     * @return The XML with the DOCTYPE added.
     */
    public static String addPublicDoctype(final String xml, final String publicName, final String publicLocation,
            final String entityFileName, final String rootElementName) {
        final String preamble = findPreamble(xml);
        final String docType = findDocumentType(xml);
        final String fixedPreamble = preamble == null ? "<?xml version='1.0' encoding='UTF-8' ?>\n" : preamble + "\n";

        // Remove any current doctype declarations
        final String fixedXML;
        if (docType != null) {
            final String tempFixedXML = preamble == null ? xml : xml.replace(preamble, "");
            fixedXML = tempFixedXML.replace(docType, "");
        } else {
            fixedXML = preamble == null ? xml : xml.replace(preamble, "");
        }

        final StringBuilder retValue = new StringBuilder(fixedPreamble);
        retValue.append("<!DOCTYPE ");
        if (rootElementName == null) {
            retValue.append("chapter");
        } else {
            retValue.append(rootElementName);
        }
        retValue.append(" PUBLIC \"" + publicName + "\" \"" + publicLocation + "\" ");

        // Add the local entity file
        if (entityFileName != null) {
            retValue.append("[\n");
            retValue.append("<!ENTITY % BOOK_ENTITIES SYSTEM \"" + entityFileName + "\">\n");
            retValue.append("%BOOK_ENTITIES;\n");
            retValue.append("]");
        }

        retValue.append(">\n");
        retValue.append(fixedXML);

        return retValue.toString();
    }

    /**
     * Cleans a string for of insignificant whitespace
     */
    protected static String cleanText(final String input) {
        if (input == null) return "";
        /* get rid of line breaks */
        String retValue = input.replaceAll("\\r\\n|\\r|\\n|\\t", " ");
        /* get rid of double spaces */
        while (retValue.indexOf("  ") != -1) retValue = retValue.replaceAll("  ", " ");

        return retValue;
    }

    /**
     * CDATA sections can not have a "]]>" in them. This method takes the input and wraps it up in one or more CDATA sections,
     * converting any "]]>" strings into "]]&gt;".
     */
    public static String wrapStringInCDATA(final String input) {
        final StringBuffer retValue = new StringBuffer("<![CDATA[");
        retValue.append(input.replaceAll(END_CDATA_RE, END_CDATA_RE + END_CDATA_REPLACE + START_CDATA));
        retValue.append("]]>");
        return retValue.toString();
    }

    /**
     * Creates an XIInclude element with a link to a file
     *
     * @param doc  The DOM Document to create the xi:include for.
     * @param file The file name/path to link to.
     * @return An xi:include element that can be used to include content from another file.
     */
    public static Element createXIInclude(final Document doc, final String file) {
        try {
            final Element xiInclude = doc.createElementNS("http://www.w3.org/2001/XInclude", "xi:include");
            xiInclude.setAttribute("href", URLEncoder.encode(file, "UTF-8"));
            xiInclude.setAttribute("xmlns:xi", "http://www.w3.org/2001/XInclude");

            return xiInclude;
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Parses a string that defines XML entities and returns a list of Entity objects. An example input string is:
     * <pre>
     *     &lt;!ENTITY ent "My Entity"&gt;
     *     &lt;!ENTITY cut "&lt;para&gt;&lt;/para&gt;"&gt;
     * </pre>
     *
     * @param entitiesString The content spec to get the entities from.
     * @return A list of Entity objects that were parsed from the string.
     */
    public static List<Entity> parseEntitiesFromString(final String entitiesString) {
        final List<Entity> retValue = new ArrayList<Entity>();

        // Check to make sure we have something to parse
        if (!isNullOrEmpty(entitiesString)) {

            /*
             * This has to be done in two steps because Xerces will not parse an entities value unless it is referenced in the XML
             * content. However since we don't know what entities we have, we need to do an initial parse to get the entity names. Afer
             * that we construct a new wrapper which references the entities and the convert it to a Document making sure that the entities
             * are expanded. Once this is done we can then look over the entities and construct the return value.
             */
            try {
                // First Pass to find the entity names used
                final String wrappedEntities = "<!DOCTYPE section [" + entitiesString + "]><section></section>";
                final Document firstPassDoc = convertStringToDocument(wrappedEntities);

                final List<String> entityNames = new ArrayList<String>();
                final NamedNodeMap entities = firstPassDoc.getDoctype().getEntities();
                for (int i = 0; i < entities.getLength(); i++) {
                    entityNames.add(entities.item(i).getNodeName());
                }

                // Build the second wrapper making sure to include all the custom entities so that they are parsed and we can get them later
                final StringBuilder wrappedEntities2 = new StringBuilder("<!DOCTYPE section [");
                wrappedEntities2.append(entitiesString);
                wrappedEntities2.append("]><section>");
                for (final String entityName : entityNames) {
                    wrappedEntities2.append("&").append(entityName).append(";");
                }
                wrappedEntities2.append("</section>");

                // Do the second pass, as now that the entities are used the value will be parsed
                final Document secondPassDoc = convertStringToDocument(wrappedEntities2.toString(), false);
                final NamedNodeMap entities2 = secondPassDoc.getDoctype().getEntities();
                for (int i = 0; i < entities2.getLength(); i++) {
                    final Entity entity = (Entity) entities2.item(i);
                    retValue.add(entity);
                }
            } catch (Exception e) {
                return retValue;
            }
        }

        return retValue;
    }

    /**
     * Checks for instances of PressGang Injections that are invalid. This will check for the following problems:
     * <ul>
     * <li>Incorrect Captialisation</li>
     * <li>Invalid Injection types (eg. InjectListItem)</li>
     * <li>Missing colons</li>
     * <li>Incorrect ID list (eg referencing Topic 10 as 10.xml)</li>
     * </ul>
     *
     * @param doc The DOM document to be checked for invalid PressGang injections.
     * @return A List of {@link InjectionError} objects that contain the invalid injection and the error messages.
     */
    public static List<InjectionError> checkForInvalidInjections(final Document doc) {
        final List<InjectionError> retValue = new ArrayList<InjectionError>();

        final List<Node> comments = getComments(doc.getDocumentElement());
        for (final Node comment : comments) {
            final Matcher match = INJECT_RE.matcher(comment.getTextContent());
            if (match.find()) {
                final String type = match.group("TYPE");
                final String colon = match.group("COLON");
                final String ids = match.group("IDS");

                final InjectionError error = new InjectionError(comment.getTextContent());

                // Check the type
                if (!VALID_INJECTION_TYPES.contains(type)) {
                    error.addMessage(
                            "\"" + type + "\" is not a valid injection type. The valid types are: " + CollectionUtilities.toSeperatedString(
                                    VALID_INJECTION_TYPES, ", "));
                }

                // Check that a colon has been specified
                if (isNullOrEmpty(colon)) {
                    error.addMessage("No colon specified in the injection.");
                }

                // Check that the id(s) are valid
                if (isNullOrEmpty(ids) || !INJECT_ID_RE.matcher(ids).matches()) {
                    if (type.equalsIgnoreCase("inject")) {
                        error.addMessage(
                                "The Topic ID in the injection is invalid. Please ensure that only the Topic ID is used. eg " +
                                        "\"Inject: 1\"");
                    } else {
                        error.addMessage(
                                "The Topic ID(s) in the injection are invalid. Please ensure that only the Topic ID is used and is " +
                                        "in a comma separated list. eg \"InjectList: 1, 2, 3\"");
                    }
                } else if (type.equalsIgnoreCase("inject") && !INJECT_SINGLE_ID_RE.matcher(ids.trim()).matches()) {
                    error.addMessage(
                            "The Topic ID in the injection is invalid. Please ensure that only the Topic ID is used. eg " + "\"Inject: " +
                                    "1\"");
                }

                // Some errors were found so add the injection to the retValue
                if (!error.getMessages().isEmpty()) {
                    retValue.add(error);
                }
            }
        }

        return retValue;
    }
}
