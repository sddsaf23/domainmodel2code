//�����޽������ĵİ�ע��
@BoundedContext(name = "Cargo")
//�����ɵ������޽�������ӳ���ϵ
@DownStreamContext(upStreamContextName = "Customer", downStreamContextType = DownStreamContextType.Default) 
//�����ɵ������޽�������ӳ���ϵ
@DownStreamContext(upStreamContextName = "CustomerShare", downStreamContextType = DownStreamContextType.Default) 
//�����ɵ������޽�������ӳ���ϵ
@DownStreamContext(upStreamContextName = "Voyage", downStreamContextType = DownStreamContextType.Default) 

package gen.model.Cargo;
import gen.annotation.BoundedContext;
import gen.annotation.Partnership;
import gen.annotation.DownStreamContext;
import gen.annotation.UpStreamContext;