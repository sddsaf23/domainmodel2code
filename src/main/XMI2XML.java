package main;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.*;

import main.domainmodeltag.*;
import main.domainmodeltag.Module;

public class XMI2XML {

    //���Ȼ�ȡ��ÿһ���ڵ�����ͣ���Ϊһ��ڵ�����ж�����ͣ���������������һ��list
    //ͬʱ��ȡһ��model id��ʵ��model���ƵĶ�Ӧ��ϵmap
    public static Map<String,List<String>> map=new HashMap<String,List<String>>();
    public static Map<String,String> id2NameMap=new HashMap<String,String>();

    //��������ģ�ͺͶ�Ӧ�ı�ǵ�ֵ�Ķ�Ӧ��ϵ
    public static Map<String, DomainObject> domainObjectMap=new HashMap<>();
    public static Map<String,DomainObject> aggregateMap =new HashMap<>();

    //��������ģ�ͺ�����ģ�Ͷ�Ӧ���޽�������֮��Ĺ�ϵ
    public static Map<String,String> class2PackageMap=new HashMap<>();

    //�����޽�������֮���������ϵ
    public static Map<String, Set<String>> dependMap=new HashMap<>();
    //�����޽�������֮��ı�������ϵ
    public static Map<String,Set<String>> dependedMap=new HashMap<>();

    public static void main(String[] args) throws Exception{
        //���ڱ��洦���ļ���·��
        String location="source.xml";

        //��ȡģ�͵�xmiԴ��
        File source=new File("source.xml");
        FileReader f=new FileReader(source);
        BufferedReader bf=new BufferedReader(f);


        String line;
        String packageId="";

        //��ģ�͵�xmi�ļ���ʽ���򵥵Ĵ���
        List<String> list=new ArrayList<>();
        while((line=bf.readLine())!=null) {

            changeXMIStr(line,list);

            getId2NameMap(id2NameMap,line);

            packageId=getCurPackageName(packageId,line);

            if(line.contains("xmi:type=\"uml:Class\"")){
                String classId = getPropertyFromStr(line.indexOf("xmi:id="),line);
                class2PackageMap.put(classId,packageId);
            }

        }

        //System.out.println(map);
        for(String key:class2PackageMap.keySet()){
            System.out.println(id2NameMap.get(key)+" belongs to "+id2NameMap.get(class2PackageMap.get(key)));
        }

        //��ȡ�����֮���������ϵ
        String classId = "";
        for(String l:list){
            getDependency(l);

            if(l.contains("xmi:type=\"uml:Class\"")) {
                classId = getPropertyFromStr(l.indexOf("xmi:id="), l);
            }

            getAssociation(l,classId);
        }

        System.out.println(dependMap);
        System.out.println(dependedMap);

        //���ɵ�xml�ļ����ļ�·��
        File target=new File("target.xml");
        target.createNewFile();

        BufferedWriter bw=new BufferedWriter(new FileWriter(target));

        //���ɵ�xml�ļ��У�����attribute�е����Ա�����""��
        for(int i=0;i<list.size();i++) {
            String s=list.get(i);
            //���ȸ���xml�ļ���ʽ��Ҫ��ɾ��xmi��ʽ�е�xmi�ֶ�
            s = changeFormatOfStr(s,map);

            //��ɾ���˶���ֶκ󣬿��ܳ���ͬһ���д�������type���������һ����ҪΪ�˴�����һ�����
            if(getTime(s,"type")==2) {
                if(s.trim().startsWith("<ownedAttribute")&&!s.contains("association")) {
                    //���������Ҫɾ���ڶ���type;
                    int secondTypeIndex=s.indexOf("type",s.indexOf("type")+1);
                    String id=getPropertyFromStr(secondTypeIndex,s);
                    String name=id2NameMap.get(id);

                    //ɾ����һ���еĵڶ���type�������type���ڵڶ�����
                    bw.write(s.substring(0,secondTypeIndex-1)+s.substring(secondTypeIndex+7+id.length(),s.length()-2)+">"+"\n");
                    bw.write(getSpaceByTime(getSpaceTime(s)+2)+"<type type=\""+name+"\"/>"+"\n");
                    bw.write(getSpaceByTime(getSpaceTime(s))+"</ownedAttribute>");
                    continue;
                }
                //�������������ֱ��ɾ��
                if(s.trim().startsWith("<ownedAttribute")&&s.contains("association")){
                    if(s.contains("/>"))
                        continue;
                    while(!list.get(i).trim().equals("</ownedAttribute>"))
                        i++;
                    continue;
                }
                //���������Ҳֱ��ɾ��
                if(s.trim().startsWith("<ownedEnd")&&s.contains("association")){
                    if(s.contains("/>"))
                        continue;
                    while(!list.get(i).trim().equals("</ownedEnd>"))
                        i++;
                    continue;
                }
            }

            //����papyrus�е�primitiveType�ĸ�ʽ�����µĵ���
            if(s.contains("type")&&getPropertyFromStr(s.indexOf("type"),s).equals("PrimitiveType")&&s.contains("href")){
                String href=getPropertyFromStr(s.indexOf("href"),s);
                String type=href.split("#")[1];
                if(type.equals("EDate"))
                    type="Date";
                bw.write(getSpaceByTime(getSpaceTime(s))+"<type type=\""+type+"\"/>"+"\n");
                continue;
            }
            bw.write(s+"\n");

            //����package��ʼ�Ĳ��֣�������Ҫ�������package�����Ρ����Ρ��Լ�����ϵ
            if(s.contains("<packagedElement type=\"Package\"")){
                String packageName = getPropertyFromStr(s.indexOf("name"),s);
                int spaceTime=getSpaceTime(s);

                for(String supplier:dependMap.getOrDefault(packageName,new HashSet<>())){
                    //���supplierҲ�����ڸð�����֤����Ӧ����һ������ϵ
                    if(dependMap.getOrDefault(supplier,new HashSet<>()).contains(packageName)){
                        String add=getSpaceByTime(spaceTime+2);
                        add+="<Partnership ";
                        add+="anotherPartnershipContext=\""+supplier+"\"/>";
                        bw.write(add+"\n");
                    }
                    //������������£�֤���������һ�������޽�������
                    else{
                        String add= getSpaceByTime(spaceTime+2);
                        add+="<DownStreamContext ";
                        add+="upStreamContextName=\""+supplier+"\" ";
                        add+="downStreamContextType=\""+"DownStreamContextType.Default\""+"/>";
                        bw.write(add+"\n");
                    }
                }

                for(String client:dependedMap.getOrDefault(packageName,new HashSet<>())){
                    //���clientҲ����������������һ���
                    if(dependMap.getOrDefault(packageName,new HashSet<>()).contains(client)){
                        continue;
                    }
                    String add=getSpaceByTime(spaceTime+2);
                    add+="<UpStreamContext ";
                    add+="downStreamContextName=\""+client+"\" ";
                    add+="upStreamContextType=\""+"UpStreamContextType.Default\""+"/>";
                    bw.write(add+"\n");
                }
            }
        }
        bw.flush();

    }

    //��ȡ�����֮��Ĺ�����ϵ
    public static void getAssociation(String line,String classId){
        if(line.trim().startsWith("<ownedAttribute")&&line.contains("association=")&&line.contains(" type=")){
            String supplier = id2NameMap.get(class2PackageMap.get(getPropertyFromStr(line.indexOf(" type="),line)));
            String client = id2NameMap.get(class2PackageMap.get(classId));

            //System.out.println(supplier+" "+client);

            if(!supplier.equals(client)) {

                dependMap.putIfAbsent(client, new HashSet<>());
                dependMap.get(client).add(supplier);

                dependedMap.putIfAbsent(supplier, new HashSet<>());
                dependedMap.get(supplier).add(client);
            }
        }
    }

    //��ȡ�����֮���������ϵ
    public static void getDependency(String line){

        if(line.contains("xmi:type=\"uml:Dependency\"")){
            //�ͻ���
            String client=id2NameMap.get(class2PackageMap.get(getPropertyFromStr(line.indexOf("client"),line)));
            //��������
            String supplier = id2NameMap.get(class2PackageMap.get(getPropertyFromStr(line.indexOf("supplier"),line)));

            //ͬһ���޽������Ĳ����ɰ�֮���������ϵ
            if(client.equals(supplier))
                return ;

            dependMap.putIfAbsent(client,new HashSet<>());
            dependMap.get(client).add(supplier);
            dependedMap.putIfAbsent(supplier,new HashSet<>());
            dependedMap.get(supplier).add(client);
            return ;
        }

    }

    //���µ�ǰ�İ���
    public static String getCurPackageName(String packageName,String line){

        if(line.contains("xmi:type=\"uml:Package\"")) {
            String newPackageName = getPropertyFromStr(line.indexOf("xmi:id="), line);
            //���а���Ӧ�İ������Լ�
            class2PackageMap.put(newPackageName,newPackageName);
            return newPackageName;
        }
        return packageName;
    }

    public static void getId2NameMap(Map<String,String> map,String s) {
        if(s.contains("xmi:id=")&&s.contains("name=")) {
            int idIndex=s.indexOf("xmi:id");
            String id=getPropertyFromStr(idIndex,s);

            int nameIndex=s.indexOf("name=");
            String name=getPropertyFromStr(nameIndex,s);

            if(id.length()>0&&name.length()>0) {
                map.put(id, name);
            }
        }
    }

    private static String getPropertyFromStr(int index,String str) {
        int time=0;
        String res="";

        while(time<2) {
            if(str.charAt(index)=='"')
                time++;
            else if(time==1)
                res=res+str.charAt(index);
            index++;
        }

        return res;
    }

    private static int getTime(String str,String slice) {
        int start=-1;
        int time=0;
        while((start=str.indexOf(slice, start+1))!=-1) {
            time++;
        }
        return time;
    }

    private static int getSpaceTime(String line){
        int index=0;
        while(index<line.length()&&line.charAt(index)==' ')
            index++;
        return index;
    }

    private static String getSpaceByTime(int time){
        StringBuilder sb=new StringBuilder();
        for(int i=0;i<time;i++)
            sb.append(" ");
        return sb.toString();
    }


    //���ȶ�xmi���ַ������򵥵Ĵ�����ȡ����ɾ��":"����
    private static void  changeXMIStr(String line,List<String> list){

        //xml�в�������xmi�ļ����ַ���
        if(line.contains("xmi:version=")||line.contains("</xmi:XMI>"))
            return;

        if(line.contains("metamodel:")) {
            String type="";
            String id="";

            for(String s:line.split(" ")) {
                if(s.contains("metamodel:")) {
                    type=s.split(":")[1];
                }
                if(s.contains("base_Class=")) {
                    id=s.split("=")[1];
                    id=id.substring(1,id.length()-3);
                }
                if(s.contains("base_Package=")){
                    id=s.split("=")[1];
                    id=id.substring(1,id.length()-3);
                }

            }
            if(id.length()>0&&type.length()>0) {
                map.putIfAbsent(id, new ArrayList<>());
                map.get(id).add(type);
                //System.out.println(type+" "+id);
                //�洢ÿ�������Եı��ֵ
                DomainObject o;
                switch (type){
                    case "Entity":
                        String identifier=getPropertyFromStr(line.indexOf("identifier"),line);
                        o=new Entity(identifier);
                        domainObjectMap.put(id,o);
                        break;
                    case "ValueObject":
                        o=new ValueObject();
                        domainObjectMap.put(id,o);
                        break;
                    case "DomainEvent":
                        identifier=getPropertyFromStr(line.indexOf("identifier"),line);
                        String timestamp=getPropertyFromStr(line.indexOf("timestamp"),line);
                        String publisher=getPropertyFromStr(line.indexOf("publisher"),line);
                        String subscriber=getPropertyFromStr(line.indexOf("subscriber"),line);
                        o=new DomainEvent(identifier,timestamp,publisher,subscriber);
                        domainObjectMap.put(id,o);
                        break;
                    case "DomainService":
                        o=new DomainService();
                        domainObjectMap.put(id,o);
                        break;
                    case "Repository":
                    	System.out.println(line);
                        String accessingDomainObject=getPropertyFromStr(line.indexOf("accessingDomainObject"),line);
                        o=new Repository(id2NameMap.get(accessingDomainObject));
                        domainObjectMap.put(id,o);
                        break;
                    case "Factory":
                        String creatingDomainObject=getPropertyFromStr(line.indexOf("creatingDomainObject"),line);
                        o=new Factory(id2NameMap.get(creatingDomainObject));
                        domainObjectMap.put(id,o);
                        break;
                    case "AggregatePart":
                        String aggregateRootType=getPropertyFromStr(line.indexOf("aggregateRootType"),line);
                        o=new AggregatePart(id2NameMap.get(aggregateRootType));
                        aggregateMap.put(id,o);
                        break;
                    case "AggregateRoot":
                        o=new AggregateRoot();
                        aggregateMap.put(id,o);
                        break;
                    //���������Ӧ��packageԪ����һ��packageʱ
                    case "BoundedContext":
                        o=new BoundedContext();
                        domainObjectMap.put(id,o);
                        break;
                    case "Aggregate":
                        o=new Aggregate();
                        domainObjectMap.put(id,o);
                        break;
                    case "Module":
                        o=new Module();
                        domainObjectMap.put(id,o);
                        break;
                    case "SharedKernel":
                        String oneContext=getPropertyFromStr(line.indexOf("oneContext"),line);
                        String anotherContext=getPropertyFromStr(line.indexOf("theOtherContext"),line);
                        oneContext=id2NameMap.get(oneContext);
                        anotherContext=id2NameMap.get(anotherContext);
                        o=new SharedKernel(oneContext,anotherContext);
                        domainObjectMap.put(id,o);
                        break;
                    default:
                        System.out.println("error!"+type+" ");
                        break;
                }
            }
            return ;
        }

        list.add(line);
        return ;
    }


    //��һ�������ڵ��������xml�ļ���ʽ
    public static String changeFormatOfStr(String str,Map<String,List<String>> map) {
        str=removeStr(str,"xmi:");
        str=removeStr(str,"uml:");

        //��element���DDD type,������ģ�ʹ��ڶ��DDD typeʱ�����������Ҫ�޸�
        if(str.contains("id=")) {
            String id="";
            List<String> dType=new ArrayList<>();
            for(String s:str.split(" ")) {
                if(s.contains("id=")) {
                    id=s.split("=")[1];
                    id=id.substring(1,id.length()-1);
                    dType.addAll(map.getOrDefault(id, new ArrayList<>()));
                }
            }
            if(dType.size()==1) {
                String s=dType.get(0);
                if(str.charAt(str.length()-2)!='/')
                    return str.substring(0,str.length()-1)+" dtype=\""+s+"\" "+domainObjectMap.get(id)+" annotion=\"\">";
                return str.substring(0,str.length()-2)+" dtype=\" "+domainObjectMap.get(id)+s+"\""+" annotion=\"\"/>";
            }else if (dType.size()==2) {
                Collections.sort(dType);

                //�������˵��dType��ʽ������
                if(!dType.get(0).startsWith("Aggregate")||dType.get(1).startsWith("Aggregate")) {
                    System.out.println("dType ��ʽ����");
                    return "";
                }

                String add =  " dtype=\""+dType.get(1)+"\" "+domainObjectMap.get(id)+" annotion=\"@"+dType.get(0)+aggregateMap.get(id)+"\"";
                if(str.charAt(str.length()-2)!='/')
                    return str.substring(0,str.length()-1)+add+">";

                return str.substring(0,str.length()-2)+add+"/>";
            }
        }

        return str;
    }


    //��һ�����������Ƴ�xmi�ļ��в���Ҫ���ַ���ǰ׺
    public static String removeStr(String source,String remove) {
        int removeLength=remove.length();
        int start=-removeLength;


        StringBuilder result=new StringBuilder();
        int pre=0;

        while((start=source.indexOf(remove, start+removeLength))!=-1) {
            result.append(source.substring(pre, start));
            pre=start+removeLength;
        }

        result.append(source.substring(pre));

        return result.toString();
    }

}
