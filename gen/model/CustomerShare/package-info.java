//���ɹ����ں�ģ��İ�ע��
@SharedKernel(name = "CustomerShare", oneContextName = "Cargo", theOtherContextName = "Customer")

	 
//�����ɵ������޽�������ӳ���ϵ
@DownStreamContext(upStreamContextName = "Customer", downStreamContextType = DownStreamContextType.Default) 
	 
//�����ɵ������޽�������ӳ���ϵ
@UpStreamContext(downStreamContextName = "Cargo", upStreamContextType = UpStreamContextType.Default) 

package gen.model.CustomerShare;

import gen.annotation.SharedKernel;
import gen.annotation.Partnership;
import gen.annotation.DownStreamContext;
import gen.annotation.UpStreamContext;