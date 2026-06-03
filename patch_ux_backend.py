import os
import re

base_dir = r"d:\Đánh giá và kiểm định\demo\demo"

# 1. Update DentalServiceRepository.java
repo_file = os.path.join(base_dir, "src/main/java/com/example/demo/repository/DentalServiceRepository.java")
with open(repo_file, 'r', encoding='utf-8') as f:
    content = f.read()

if "findTopByOrderByIdDesc()" not in content:
    if "import java.util.Optional;" not in content:
        content = content.replace("import org.springframework.stereotype.Repository;", "import org.springframework.stereotype.Repository;\nimport java.util.Optional;")
    content = content.replace("boolean existsByCodeAndIdNot(String code, Long id);", "boolean existsByCodeAndIdNot(String code, Long id);\n    Optional<DentalService> findTopByOrderByIdDesc();")

with open(repo_file, 'w', encoding='utf-8') as f:
    f.write(content)

# 2. Update DentalServiceService.java
srv_file = os.path.join(base_dir, "src/main/java/com/example/demo/service/DentalServiceService.java")
with open(srv_file, 'r', encoding='utf-8') as f:
    content = f.read()

# Modify createService to auto-generate code
old_create = """    public DentalService createService(DentalService service) {
        validateServiceData(service, null);
        
        if (service.getStatus() == null) {
            service.setStatus("Áp dụng");
        }
        
        return serviceRepository.save(service);
    }"""

new_create = """    public DentalService createService(DentalService service) {
        // Tự động sinh mã dịch vụ
        serviceRepository.findTopByOrderByIdDesc().ifPresentOrElse(
            latest -> {
                String latestCode = latest.getCode();
                try {
                    int nextNum = Integer.parseInt(latestCode.substring(2)) + 1;
                    service.setCode(String.format("DV%03d", nextNum));
                } catch (Exception e) {
                    service.setCode("DV001");
                }
            },
            () -> service.setCode("DV001")
        );

        validateServiceData(service, null);
        
        if (service.getStatus() == null) {
            service.setStatus("Áp dụng");
        }
        
        return serviceRepository.save(service);
    }"""

content = content.replace(old_create, new_create)

# Remove code validation from validateServiceData because it's auto-generated now. Actually, since it's auto-generated, it will always be there, but let's leave it or ensure it's correct.
# Wait, for updating, code is passed. So validateServiceData shouldn't fail. The user said: "khóa mờ (Read-only) ô nhập Mã dịch vụ...".
# So the frontend will still send the code on update.
with open(srv_file, 'w', encoding='utf-8') as f:
    f.write(content)

print("Backend patched successfully")
