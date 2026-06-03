import os
import re

filepath = r"d:\Đánh giá và kiểm định\demo\demo\src\main\resources\static\schedules.html"

with open(filepath, 'r', encoding='utf-8') as f:
    content = f.read()

# 1. Update max-width of content-shell
content = content.replace(".content-shell { max-width: 960px;", ".content-shell { max-width: 1400px;")

# 2. Add .disabled CSS class for shift-block
css_insert = """      .shift-block:hover { border-color: #cbd5e1; background: #f1f5f9; }
      .shift-block.disabled { opacity: 0.4; background: #e5e7eb; cursor: not-allowed; border-color: #d1d5db; }
      .shift-block.disabled:hover { background: #e5e7eb; border-color: #d1d5db; }"""
content = content.replace("      .shift-block:hover { border-color: #cbd5e1; background: #f1f5f9; }", css_insert)

# 3. Fix the rendering loop to use hasActiveShift and apply disabled style
old_loop = """                    // Render Shifts
                    const shifts = ['Sáng', 'Chiều', 'Tối'];
                    shifts.forEach(st => {
                        let shiftClass = st === 'Sáng' ? 'sang' : st === 'Chiều' ? 'chieu' : 'toi';
                        let blockClick = isAdmin ? `onclick="toggleShiftAdmin('${isoStr}', '${st}')"` : `onclick="toggleShiftDoctor('${isoStr}', '${st}')"`;
                        
                        let tagsHtml = '';
                        let daySchedules = allSchedules.filter(s => s.shiftDate === isoStr && s.shiftType === st);"""

new_loop = """                    // Render Shifts
                    const shifts = ['Sáng', 'Chiều', 'Tối'];
                    shifts.forEach(st => {
                        let shiftClass = st === 'Sáng' ? 'sang' : st === 'Chiều' ? 'chieu' : 'toi';
                        
                        // Check if shift is active in UC2.2
                        let isActive = activeShifts.some(s => s.shiftDate === isoStr && s.shiftType === st);
                        
                        let blockClick = '';
                        let disabledClass = '';
                        
                        if (isActive) {
                            blockClick = isAdmin ? `onclick="toggleShiftAdmin('${isoStr}', '${st}')"` : `onclick="toggleShiftDoctor('${isoStr}', '${st}')"`;
                        } else {
                            disabledClass = 'disabled';
                        }
                        
                        let tagsHtml = '';
                        let daySchedules = allSchedules.filter(s => s.shiftDate === isoStr && s.shiftType === st);"""

# Because of encoding issues (Sáng vs SAng), I will use regex to find the loop.
# Let's search by a unique part that doesn't have encoding issues.

pattern = re.compile(r"const shifts = \['.*?\];\s*shifts\.forEach\(st => \{.*?(let daySchedules = allSchedules\.filter)", re.DOTALL)

def replace_logic(match):
    return """const shifts = ['Sáng', 'Chiều', 'Tối'];
                    shifts.forEach(st => {
                        let shiftClass = st === 'Sáng' ? 'sang' : st === 'Chiều' ? 'chieu' : 'toi';
                        
                        // Check if shift is active
                        let isActive = false;
                        if (activeShifts) {
                            isActive = activeShifts.some(s => s.shiftDate === isoStr && s.shiftType === st);
                        }
                        
                        let blockClick = '';
                        let disabledClass = '';
                        
                        if (isActive) {
                            blockClick = isAdmin ? `onclick="toggleShiftAdmin('${isoStr}', '${st}')"` : `onclick="toggleShiftDoctor('${isoStr}', '${st}')"`;
                        } else {
                            disabledClass = 'disabled';
                        }
                        
                        let tagsHtml = '';
                        """ + match.group(1)

content = pattern.sub(replace_logic, content)

# Replace the shift-block div rendering to include disabledClass
old_div = """                          cellHtml += `
                              <div class="shift-block" ${blockClick}>
                                  <div class="shift-title ${shiftClass}">${st}</div>
                                  ${tagsHtml}
                              </div>
                          `;"""

new_div = """                          cellHtml += `
                              <div class="shift-block ${disabledClass}" ${blockClick}>
                                  <div class="shift-title ${shiftClass}">${st}</div>
                                  ${tagsHtml}
                              </div>
                          `;"""
content = content.replace(old_div, new_div)

# Ensure 'Sáng', 'Chiều', 'Tối' literal matches activeShifts
# The activeShifts API returns "Sáng", "Chiều", "Tối". Let's make sure the array matches.
# It is already 'Sáng', 'Chiều', 'Tối' in new_loop.

with open(filepath, 'w', encoding='utf-8') as f:
    f.write(content)

print("Patched schedules.html for disabled shifts and width!")
