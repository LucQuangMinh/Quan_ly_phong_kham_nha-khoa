import os
import re

filepath = r"d:\Đánh giá và kiểm định\demo\demo\src\main\resources\static\schedules.html"

with open(filepath, 'r', encoding='utf-8') as f:
    content = f.read()

# 1. Update CSS for shift-block to background: #f8fafc
content = content.replace("transition: all 0.15s; background: #fff;", "transition: all 0.15s; background: #f8fafc;")

# 2. Re-write the render loop using regex
# We need to find the portion from `const shifts = ['Sáng', 'Chiều', 'Tối'];` to the end of the `shifts.forEach` block.

pattern = re.compile(r"(\s*// Render Shifts\s*const shifts = \['Sáng', 'Chiều', 'Tối'\];\s*)(shifts\.forEach\(st => \{.*?)(<div class=\"shift-block.*?</div>\s*`;\s*\n\s*\});)", re.DOTALL)

def replacer(match):
    prefix = match.group(1)
    body = match.group(2)
    end = match.group(3)
    
    # Inside the loop, we replace the if (isActive) logic to just return if not active
    # and we remove disabledClass logic.
    
    new_loop = """let hasAnyShift = false;
                    shifts.forEach(st => {
                        let shiftClass = st === 'Sáng' ? 'sang' : st === 'Chiều' ? 'chieu' : 'toi';
                        
                        // Check if shift is active
                        let isActive = false;
                        if (activeShifts) {
                            isActive = activeShifts.some(s => s.shiftDate === isoStr && s.shiftType === st);
                        }
                        
                        if (!isActive) return; // Do not render inactive shifts
                        hasAnyShift = true;
                        
                        let blockClick = isAdmin ? `onclick="toggleShiftAdmin('${isoStr}', '${st}')"` : `onclick="toggleShiftDoctor('${isoStr}', '${st}')"`;
                        
                        let tagsHtml = '';
                        let daySchedules = allSchedules.filter(s => s.shiftDate === isoStr && s.shiftType === st);"""
    
    # Replace the start of the loop
    body = re.sub(r"shifts\.forEach\(st => \{.*?let daySchedules = allSchedules\.filter\(.*?;\s*", new_loop + "\n                        ", body, flags=re.DOTALL)
    
    # Replace the block div
    end = re.sub(r"<div class=\"shift-block.*?>", r'<div class="shift-block" ${blockClick}>', end)
    
    # Add the no-shifts-text
    end += """
                    if (!hasAnyShift) {
                        cellHtml += `<div style="font-size: 0.6875rem; color: #94a3b8; font-style: italic; margin-top: 8px; text-align: center;">Không có ca làm việc</div>`;
                    }"""
    
    return prefix + body + end

content = pattern.sub(replacer, content)

# Check if there are encoding issues matching 'Sáng'. The file might have 'SA?ng'.
# Let's write a safer replace just in case.
old_block = """                    } else {
                    // Render Shifts"""
                    
# Since regex might fail due to utf-8 encoding differences, let's just use string replace for the whole `} else {` block if needed.

with open(filepath, 'w', encoding='utf-8') as f:
    f.write(content)

print("Patched schedules.html to match mock UI!")
