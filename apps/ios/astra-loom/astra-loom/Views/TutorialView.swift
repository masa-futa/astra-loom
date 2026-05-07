import SwiftUI

/// 操作ガイド（チュートリアル）ビュー
struct TutorialView: View {
    let onDismiss: () -> Void

    @State private var currentPage = 0
    private let totalPages = 3

    var body: some View {
        ZStack {
            // 半透明背景
            Color.black.opacity(0.85)
                .ignoresSafeArea()
                .onTapGesture {
                    onDismiss()
                }

            VStack(spacing: 40) {
                Spacer()

                // ページインジケーター
                HStack(spacing: 8) {
                    ForEach(0..<totalPages, id: \.self) { index in
                        Circle()
                            .fill(currentPage == index ? Color.white : Color.white.opacity(0.3))
                            .frame(width: 8, height: 8)
                    }
                }

                // コンテンツ
                Group {
                    switch currentPage {
                    case 0:
                        page1_ViewportPan
                    case 1:
                        page2_TimeScrub
                    case 2:
                        page3_OtherFeatures
                    default:
                        EmptyView()
                    }
                }
                .frame(height: 400)

                Spacer()

                // ナビゲーションボタン
                HStack(spacing: 20) {
                    if currentPage > 0 {
                        Button("戻る") {
                            withAnimation {
                                currentPage -= 1
                            }
                        }
                        .buttonStyle(.bordered)
                    }

                    Spacer()

                    if currentPage < totalPages - 1 {
                        Button("次へ") {
                            withAnimation {
                                currentPage += 1
                            }
                        }
                        .buttonStyle(.borderedProminent)
                    } else {
                        Button("始める") {
                            onDismiss()
                        }
                        .buttonStyle(.borderedProminent)
                    }
                }
                .padding(.horizontal, 40)
                .padding(.bottom, 40)
            }
        }
        .foregroundColor(.white)
    }

    // ページ1: 1本指スワイプ - 視点移動
    private var page1_ViewportPan: some View {
        VStack(spacing: 30) {
            Image(systemName: "hand.point.up.left.fill")
                .font(.system(size: 80))
                .symbolEffect(.pulse)

            Text("空を見渡す")
                .font(.largeTitle)
                .fontWeight(.bold)

            Text("1本指でスワイプ")
                .font(.title2)

            VStack(alignment: .leading, spacing: 12) {
                HStack(spacing: 12) {
                    Image(systemName: "arrow.left.arrow.right")
                    Text("左右: 東西に視点を回転")
                }
                HStack(spacing: 12) {
                    Image(systemName: "arrow.up.arrow.down")
                    Text("上下: 仰角を変更")
                }
            }
            .font(.body)
            .padding()
            .background(.white.opacity(0.1))
            .cornerRadius(12)
        }
        .padding()
    }

    // ページ2: 2本指スワイプ - 時間変更
    private var page2_TimeScrub: some View {
        VStack(spacing: 30) {
            Image(systemName: "hand.point.up.left.and.text.fill")
                .font(.system(size: 80))
                .symbolEffect(.pulse)

            Text("時間を操る")
                .font(.largeTitle)
                .fontWeight(.bold)

            Text("2本指で左右スワイプ")
                .font(.title2)

            VStack(alignment: .leading, spacing: 12) {
                HStack(spacing: 12) {
                    Image(systemName: "arrow.left")
                    Text("左: 時間を進める（未来へ）")
                }
                HStack(spacing: 12) {
                    Image(systemName: "arrow.right")
                    Text("右: 時間を戻す（過去へ）")
                }
            }
            .font(.body)
            .padding()
            .background(.white.opacity(0.1))
            .cornerRadius(12)

            Text("星が東から西へ流れる様子を楽しめます")
                .font(.caption)
                .opacity(0.8)
        }
        .padding()
    }

    // ページ3: その他の機能
    private var page3_OtherFeatures: some View {
        VStack(spacing: 30) {
            Image(systemName: "sparkles")
                .font(.system(size: 80))
                .symbolEffect(.pulse)

            Text("その他の機能")
                .font(.largeTitle)
                .fontWeight(.bold)

            VStack(alignment: .leading, spacing: 16) {
                FeatureRow(icon: "star.fill", title: "星をタップ", description: "詳細情報を表示")
                FeatureRow(icon: "viewfinder", title: "視野角調整", description: "広角・標準・望遠")
                FeatureRow(icon: "location.fill", title: "観測地点", description: "世界各地の星空")
                FeatureRow(icon: "clock.fill", title: "時刻設定", description: "任意の時刻を指定")
            }
            .padding()
            .background(.white.opacity(0.1))
            .cornerRadius(12)
        }
        .padding()
    }
}

private struct FeatureRow: View {
    let icon: String
    let title: String
    let description: String

    var body: some View {
        HStack(spacing: 12) {
            Image(systemName: icon)
                .frame(width: 24)
            VStack(alignment: .leading, spacing: 2) {
                Text(title)
                    .fontWeight(.semibold)
                Text(description)
                    .font(.caption)
                    .opacity(0.8)
            }
        }
    }
}

#Preview {
    TutorialView(onDismiss: {})
}
